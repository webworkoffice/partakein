package in.partake.page.event;

import in.partake.application.PartakeSession;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.page.base.PartakeBasePage;
import in.partake.resource.I18n;
import in.partake.service.EventService;
import in.partake.service.UserService;
import in.partake.util.Util;
import in.partake.view.Helper;
import in.partake.wicket.component.InvisibleComponent;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class EventShowPage extends PartakeBasePage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(EventShowPage.class);

    public EventShowPage(PageParameters params) {
        String eventId = params.get("id").toString();
        try {
            EventEx event = EventService.get().getEventExById(eventId);
            
            if (event == null) {
                try {
                    if (EventService.get().isRemoved(eventId)) {
                        renderPage(EventRemovedPage.class);
                        return;
                    }
                } catch (DAOException e) {
                    logger.error(I18n.t(I18n.DATABASE_ERROR), e);
                    renderDBError();
                    return;
                }
                
                renderNotFound();
                return;
            }

            
            UserEx user = PartakeSession.get().getCurrentUser(); 

            if (event.isPrivate()) {
                // owner および manager は見ることが出来る。
                String passcode = (String) PartakeSession.get().get("event:" + eventId);
                if (user != null && event.hasPermission(user, UserPermission.EVENT_PRIVATE_EVENT)) {
                    // OK. You have the right to show this event.
                } else if (StringUtils.equals(event.getPasscode(), passcode)) {
                    // OK. The same passcode. 
                } else {
                    // public でなければ、passcode を入れなければ見ることが出来ない
                    renderPage(EventPasscodePage.class, new PageParameters().add("id", eventId));
                    return;
                }
            }

            
            add(new Label("event.title", event.getTitle()));
            add(new Label("event.summary", event.getSummary()));

            add(new ExternalLink("event.feedlink", "/feed/event/" + event.getFeedId()));
            add(new Label("event.begindate", Helper.readableDate(event.getBeginDate())));
            add(new Label("event.deadline", event.getDeadline() != null ? Helper.readableDate(event.getDeadline()) : Helper.readableDate(event.getBeginDate())));
            add(new Label("event.category", EventCategory.getReadableCategoryName(event.getCategory())));
            add(new Label("event.capacity", event.getCapacity() != 0 ? String.valueOf(event.getCapacity()) : "-"));
            
            add(new Label("event.place", event.getPlace() != null ? event.getPlace() : "-"));
            add(new Label("event.address", event.getAddress() != null ? event.getAddress() : "-"));
            add(new Label("event.url", event.getUrl() != null ? event.getUrl() : "-"));
            add(new Label("event.manager", String.format("%s (%s)", event.getOwner().getTwitterLinkage().getName(), event.getOwner().getScreenName())));
            if (StringUtils.isBlank(event.getHashTag())) {
                add(new InvisibleComponent("event.hashtag"));
            } else {
                add(new ExternalLink("event.hashtag", "http://twitter.com/#search?q=" + Util.encodeURIComponent(event.getHashTag()), event.getHashTag()));
            }
            add(new ExternalLink("event.shortenedurl", event.getShortenedURL(), event.getShortenedURL()));
            
            // 
            add(new InvisibleComponent("event.related"));
            
            //add(new WebComponent("event.description", new Model<String>(Helper.cleanupHTML(event.getDescription()))));
            add(new InvisibleComponent("event.description"));
            
            add(new EventCommentPanel("event.commentboard", event));
            
            if (event.hasPermission(user, UserPermission.EVENT_EDIT)) {
                add(new EventManagementPanel("event.management", event));
            } else {
                add(new InvisibleComponent("event.management"));
            }
            
            ParticipationStatus status; 
            if (user != null) {
                status = UserService.get().getParticipationStatus(user.getId(), event.getId());
            } else {
                status = ParticipationStatus.NOT_ENROLLED;
            }
            
            add(new EventEnrollmentPanel("event.enrollment", event, user, status));
            add(new EventPromotionPanel("event.promotion", event));
            add(new EventParticipantsPanel("event.participants", event));
        } catch (DAOException e) {
            renderDBError();
        }
    }
}
