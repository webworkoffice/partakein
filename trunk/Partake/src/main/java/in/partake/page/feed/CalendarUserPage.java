package in.partake.page.feed;

import in.partake.controller.CalendarsController;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.User;
import in.partake.page.base.PartakeCalendarPage;
import in.partake.resource.I18n;
import in.partake.service.UserService;

import java.io.IOException;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class CalendarUserPage extends PartakeCalendarPage {
    /** */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(CalendarsController.class);
        
    public CalendarUserPage(PageParameters params) {
        String calendarId = params.get("id").toOptionalString();

        if (StringUtils.isBlank(calendarId)) {
            renderInvalidRequest("指定されたカレンダー ID に対応するユーザーは存在しません。");
            return;
        }

        if (calendarId.endsWith(".ics")) {
            calendarId = calendarId.substring(0, calendarId.length() - 4);
        }
        
        try {
            // TODO: これは CalendarService.get().getEnrolledEventsByCalendarId 的ななにかにしなければならない。
            User user = UserService.get().getUserFromCalendarId(calendarId);
            if (user == null) {
                renderInvalidRequest("指定されたカレンダー ID に対応するユーザーは存在しません。");
                return;
            }
            
            Calendar calendar = createCalendarSkeleton();
            
            // for all events the user will participate ...
            List<Event> enrolledEvents = UserService.get().getEnrolledEvents(user.getId());
            for (Event event : enrolledEvents) {
                if (event == null) { continue; }
                addToCalendar(calendar, event);
            }
            
            renderCalendar(calendar);
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
        } catch (IOException e) {
            logger.error("IOException occured.", e);
            renderError("IOException occured.");
        } catch (ValidationException e) {
            logger.error("Calendar Validation Exception occured.", e);
            renderError("Calendar Validation Exception occured.");
        }
    }
}
