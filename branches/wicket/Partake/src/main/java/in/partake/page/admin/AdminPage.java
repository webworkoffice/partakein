package in.partake.page.admin;

import in.partake.model.dao.DAOException;
import in.partake.page.base.PartakeBasePage;
import in.partake.resource.I18n;
import in.partake.service.EventService;
import in.partake.service.EventService.EventCount;
import in.partake.service.UserService;
import in.partake.service.UserService.UserCount;

import java.text.NumberFormat;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

public class AdminPage extends PartakeBasePage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AdminPage.class);

    
    public AdminPage() {
        try {
            UserCount userCount = UserService.get().countUsers();
            EventCount eventCount = EventService.get().countEvents();
            NumberFormat format = NumberFormat.getInstance();
            
            add(new Label("user.count", format.format(userCount.user)));
            add(new Label("user.count.active", format.format(userCount.activeUser)));
            add(new Label("event.count", format.format(eventCount.numEvent)));
            add(new Label("event.count.private", format.format(eventCount.numPrivateEvent)));
            
            add(new Link<String>("event.index.recreate") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    try {
                        EventService.get().recreateEventIndex();
                        setResponsePage(AdminPage.class);
                    } catch (DAOException e) {
                        logger.error(I18n.t(I18n.DATABASE_ERROR), e);
                        renderDBError();
                    }
                }                
            });
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
        }
    }
}
