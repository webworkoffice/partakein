package in.partake.page.user;

import in.partake.application.PartakeSession;
import in.partake.controller.EventsFeedController;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
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
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

public class MyPage extends PartakeBasePage {
    private static final Logger logger = Logger.getLogger(MyPage.class);
    private static final long serialVersionUID = 1L;

    public MyPage() {
        PartakeSession session = PartakeSession.get();
        if (!session.isLoggedIn()) {
            renderLoginRequired();
            return;
        }
        
        UserEx user = session.getCurrentUser();
                
        add(new Label("screenName.0", user.getScreenName()));
        add(new Label("screenName.1", user.getScreenName()));
        
        
       
        try {
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
            
            renderTable("managedEvents",  ownedEvents, "noManagedEvents");
            renderTable("enrolledEvents", enrolled,    "noEnrolledEvents");
            renderTable("finishedEvents", finished,    "noFinishedEvents");
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
        }
    }
    
    private void renderTable(String id, List<Event> events, String idIfEmpty) {
        if (CollectionUtils.isEmpty(events)) {
            add(new InvisibleComponent(id));               
            add(new Label(idIfEmpty));
            
        } else {
            add(new ListView<Event>(id, events) {
                private static final long serialVersionUID = 1L;
    
                @Override
                protected void populateItem(final ListItem<Event> item) {
                    Event event = item.getModelObject();
                    
                    item.add(new ContextImage("isPrivate", "/images/private.png") {
                        private static final long serialVersionUID = 1L;
    
                        @Override
                        public boolean isVisible() {
                            return item.getModelObject().isPrivate();
                        }
                    });
    
                    item.add(new ExternalLink("eventURL", event.getEventURL(), event.getTitle()));
                    item.add(new Label("beginDate", Helper.readableDate(event.getBeginDate())));
                    item.add(new Label("capacity", Helper.readableCapacity(event.fetchNumOfEnrolledUsers(), event.getCapacity())));
                }
            });
            
            add(new InvisibleComponent(idIfEmpty));
        }
    }
}
