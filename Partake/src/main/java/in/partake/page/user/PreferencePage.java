package in.partake.page.user;

import in.partake.application.PartakeSession;
import in.partake.controller.EventsFeedController;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.UserPreference;
import in.partake.page.base.PartakeBasePage;
import in.partake.resource.Constants;
import in.partake.resource.I18n;
import in.partake.resource.PartakeProperties;
import in.partake.service.CalendarService;
import in.partake.service.UserService;
import in.partake.wicket.component.InvisibleComponent;

import java.util.List;

import javax.persistence.Column;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

public class PreferencePage extends PartakeBasePage {
    private static final Logger logger = Logger.getLogger(EventsFeedController.class);
    private static final long serialVersionUID = 1L;

    public PreferencePage() {
        PartakeSession session = PartakeSession.get();
        if (!session.isLoggedIn()) {
            renderLoginRequired();
            return;
        }
        
        try {
            final UserEx user = session.getCurrentUser();
            final UserPreference pref = UserService.get().getUserPreference(user.getId()).copy(); 
            
            // Associated OpenIDs 
            List<String> identifiers = UserService.get().getOpenIDIdentifiers(user.getId());
            if (CollectionUtils.isEmpty(identifiers)) {
                add(new InvisibleComponent("associatedOpenID"));
                add(new Label("noAssociatedOpenID"));
            } else {
                add(new InvisibleComponent("noAssociatedOpenID"));
                RepeatingView associatedOpenIDView = new RepeatingView("associatedOpenID");
                for (String identifier : identifiers) {
                    associatedOpenIDView.add(new Label(associatedOpenIDView.newChildId(), identifier));
                }
                add(associatedOpenIDView);
            }

            // form
            CompoundPropertyModel<UserPreference> userPrefModel = new CompoundPropertyModel<UserPreference>(pref);
            Form<UserPreference> preferenceForm = new Form<UserPreference>("preference", userPrefModel) {
                private static final long serialVersionUID = 1L;
                
                @Override
                protected void onSubmit() {
                    try {
                        // TODO: session に結びついているはずなので、とくにチェックはいらないはず？
                        UserService.get().setUserPreference(pref);
                        setResponsePage(PreferencePage.class);
                    } catch (DAOException e) {
                        logger.error(I18n.t(I18n.DATABASE_ERROR), e);
                        renderDBError();
                    }
                }
            };

            preferenceForm.add(new CheckBox("receivingTwitterMessage"));
            preferenceForm.add(new CheckBox("profilePublic"));
            preferenceForm.add(new CheckBox("tweetingAttendanceAutomatically"));
            add(preferenceForm);
            
            // calendarURL
            if (StringUtils.isEmpty(user.getCalendarId())) {
                add(new InvisibleComponent("calendarURL"));
                add(new Label("noCalendarURL"));
            } else {
                String calendarURL = PartakeProperties.get().getTopPath() + "/calendars/" + user.getCalendarId() + ".ics";  
                add(new InvisibleComponent("noCalendarURL"));
                add(new TextField<String>("calendarURL", new Model<String>(calendarURL)));
            }
            
            // revokeCalendar
            Form<String> revokeCalendarForm = new Form<String>("revokeCalendar") {
                private static final long serialVersionUID = 1L;
                @Override
                protected void onSubmit() {
                    try {
                        CalendarService.get().revokeCalendar(user);
                        
                        // TODO: Unfortunately, the [user] must be updated to reflect this calendar revocation.
                        // For convenient way, we retrieve user again, and set it to the session.           
                        UserEx newUser = UserService.get().getUserExById(user.getId());
                        PartakeSession.get().setCurrentUser(newUser);
                        
                        setResponsePage(PreferencePage.class);
                    } catch (DAOException e) {
                        logger.error(I18n.t(I18n.DATABASE_ERROR));
                        renderDBError();
                    }
                }
            };
            add(revokeCalendarForm);
            
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR));
            renderDBError();
        }
                
        

    }
    

}
