package in.partake.controller.action.calendar;

import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.User;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.commons.lang.StringUtils;

public class ShowCalendarAction extends AbstractCalendarAction {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException {
        String calendarId = getParameter("calendarId");
        if (StringUtils.isEmpty(calendarId))
            return renderInvalid(UserErrorCode.MISSING_CALENDAR_ID);

        // TODO: これは CalendarService.get().getEnrolledEventsByCalendarId 的ななにかにしなければならない。
        try {
            User user = DeprecatedUserDAOFacade.get().getUserFromCalendarId(calendarId);
            if (user == null)
                return renderNotFound();

            Calendar calendar = createCalendarSkeleton();

            // for all events the user will participate ...
            List<Event> enrolledEvents = DeprecatedUserDAOFacade.get().getEnrolledEvents(user.getId());
            for (Event event : enrolledEvents) {
                if (event == null)
                    continue;
                addToCalendar(calendar, event);
            }

            InputStream is = outputCalendar(calendar); 
            return renderInlineStream(is, "text/calendar; charset=utf-8");
        } catch (IOException e) {
            return renderError(ServerErrorCode.CALENDAR_CREATION_FAILURE, e);
        } catch (ValidationException e) {
            return renderError(ServerErrorCode.CALENDAR_INVALID_FORMAT, e);
        }
    }
}
