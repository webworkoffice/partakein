package in.partake.page.user;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.UserPreference;
import in.partake.page.base.PartakeBasePage;
import in.partake.resource.I18n;
import in.partake.service.EventService;
import in.partake.service.UserService;
import in.partake.view.Helper;
import in.partake.wicket.component.InvisibleComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class UserShowPage extends PartakeBasePage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(UserShowPage.class);

    public UserShowPage(PageParameters params) {
        String userId = params.get("id").toOptionalString();
        
        if (StringUtils.isEmpty(userId)) {
            renderInvalidRequest("ユーザー ID が指定されませんでした。");
            return;
        }

        try {
            UserEx user = UserService.get().getUserExById(userId);
            if (user == null) {
                renderInvalidRequest("指定されたユーザー ID は存在しませんでした。");
                return;
            }
            
            UserPreference pref = UserService.get().getUserPreference(userId);
            if (pref == null) {
                renderError("ユーザー設定を取得できませんでした。");
                return;
            }
            
            // private であれば表示しない
            if (!pref.isProfilePublic()) {
                renderRedirect("/users/private");
                return;
            }
            //　実際に表示 
            renderUser(user);
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
        }
                

    }
    
    private void renderUser(UserEx user) throws DAOException {
        add(new Label("screenName.0", user.getScreenName()));
        add(new Label("screenName.1", user.getScreenName()));
        add(new ExternalLink("twitterLink", "http://twitter.com/" + user.getScreenName()));

        List<Event> ownedEvents = EventService.get().getEventsOwnedBy(user);
        List<Event> enrolledEvents = UserService.get().getEnrolledEvents(user.getId());
        
        List<Event> enrolled = new ArrayList<Event>();
        List<Event> finished = new ArrayList<Event>();

        Date now = new Date();
        for (Event e : enrolledEvents) {
            if (e == null) { continue; }
            if (e.getBeginDate().before(now)) {
                finished.add(e);
            } else {
                enrolled.add(e);
            }
        }
        
        Collections.sort(enrolled, Event.getComparatorBeginDateAsc());
        Collections.sort(finished, Event.getComparatorBeginDateAsc());
        
        renderTable("managedEvents",  ownedEvents, "noManagedEvents",  user.getId(), true);
        renderTable("enrolledEvents", enrolled,    "noEnrolledEvents", user.getId(), false);
        renderTable("finishedEvents", finished,    "noFinishedEvents", user.getId(), false);
    }
    
    private void renderTable(String id, List<Event> events, String idIfEmpty, final String userId, final boolean showsCapacity) {
        if (CollectionUtils.isEmpty(events)) {
            add(new InvisibleComponent(id));               
            add(new Label(idIfEmpty));
        } else {
            add(new ListView<Event>(id, events) {
                private static final long serialVersionUID = 1L;
    
                @Override
                protected void populateItem(final ListItem<Event> item) {
                    Event event = item.getModelObject();
                    
                    item.add(new ExternalLink("eventURL", event.getEventURL(), event.getTitle()));
                    item.add(new Label("beginDate", Helper.readableDate(event.getBeginDate())));
                    if (showsCapacity) {
                        item.add(new Label("capacity", Helper.readableCapacity(event.fetchNumOfEnrolledUsers(), event.getCapacity())));
                    } else {
                        String status = Helper.enrollmentStatus(userId, event);
                        item.add(new Label("status", status));
                    }
                }
            });
            
            add(new InvisibleComponent(idIfEmpty));
        }
    }

}
