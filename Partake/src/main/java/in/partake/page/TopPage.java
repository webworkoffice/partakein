package in.partake.page;

import in.partake.application.PartakeSession;
import in.partake.controller.ToppageController;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.page.base.PartakeBasePage;
import in.partake.page.event.EventMiniPanel;
import in.partake.resource.I18n;
import in.partake.service.EventService;
import in.partake.wicket.component.InvisibleComponent;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.RepeatingView;

public class TopPage extends PartakeBasePage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ToppageController.class);
    private static final int DISPLAYED_EVENTS = 5;
    
    public TopPage() {
        // 最近登録されたイベントを出す。
        if (PartakeSession.get().isLoggedIn()) {
            WebMarkupContainer container = new WebMarkupContainer("myRecentEvent");
            
            UserEx user = PartakeSession.get().getCurrentUser();
            
            try {
                // TODO: ３つに抑えるのは Service でやるべき
                List<Event> enrolledEvents = EventService.get().getUnfinishedEnrolledEvents(user.getId());
                if (enrolledEvents != null && enrolledEvents.size() > 3) {
                    enrolledEvents = enrolledEvents.subList(0, 3);
                }
                List<Event> managingEvents = EventService.get().getUnfinishedEventsOwnedBy(user.getId());
                if (managingEvents != null && managingEvents.size() > 3) {
                    managingEvents = managingEvents.subList(0, 3);
                }
                
                renderMyRecentEvents(container, enrolledEvents, "recentEnrolledEvent", "noRecentEnrolledEvent", "登録");
                renderMyRecentEvents(container, managingEvents, "recentManagingEvent", "noRecentManagingEvent", "管理");
            } catch (DAOException e) {
                logger.error(I18n.t(I18n.DATABASE_ERROR));
                renderDBError();
            }
            
            add(container);
        } else {
            add(new InvisibleComponent("myRecentEvent"));
        }
        
        // 最近のイベントを出す
        try {
            List<Event> events = EventService.get().getRecentEvents(DISPLAYED_EVENTS);
            renderRecentEvent(events);
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR));
            renderDBError();
        }

    }

    private void renderMyRecentEvents(WebMarkupContainer container, List<Event> events, final String presentId, String absentId, String what) {
        if (CollectionUtils.isEmpty(events)) {
            container.add(new InvisibleComponent(presentId));
            container.add(new Label(absentId, "直近の" + what + "しているイベントはありません"));
        } else {
            container.add(new InvisibleComponent(absentId));
            container.add(new ListView<Event>(presentId, events) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void populateItem(ListItem<Event> item) {
                    Event event = item.getModelObject();
                    if (event == null) { /* Hmmm.... */ }
                    item.add(new ExternalLink(presentId + ".title", event.getEventURL(), event.getTitle()));
                }
            });
        }
    }
    
    private void renderRecentEvent(List<Event> events) {
        RepeatingView view = new RepeatingView("recentEvent");
        for (Event event : events) {
            if (event == null) { continue; }
            view.add(new EventMiniPanel(view.newChildId(), event)); 
        }
        add(view);
    }
}
