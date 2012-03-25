package in.partake.controller.action.calendar;

import in.partake.base.CalendarUtil;
import in.partake.base.PartakeException;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.model.dto.CalendarLinkage;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Event;
import in.partake.model.dto.User;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

public class ShowCalendarAction extends AbstractCalendarAction {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        String calendarId = getValidIdParameter("calendarId",
                UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        // TODO: CalendarLinkage should have cache. Maybe ShowCalendarTransaction should return
        // InputStream instead of Calendar?
        Calendar calendar = new ShowCalendarTransaction(calendarId).execute();
        try {
            InputStream is = CalendarUtil.outputCalendar(calendar); 
            return renderInlineStream(is, "text/calendar; charset=utf-8");
        } catch (IOException e) {
            return renderError(ServerErrorCode.CALENDAR_CREATION_FAILURE, e);
        } catch (ValidationException e) {
            return renderError(ServerErrorCode.CALENDAR_INVALID_FORMAT, e);
        }
    }
}

class ShowCalendarTransaction extends Transaction<Calendar> {
    private String calendarId;

    public ShowCalendarTransaction(String calendarId) {
        this.calendarId = calendarId;
    }

    @Override
    protected Calendar doExecute(PartakeConnection con) throws DAOException, PartakeException {
        // CalendarLinkage should have cache.
        CalendarLinkage calendarLinkage = DBService.getFactory().getCalendarAccess().find(con, calendarId);
        if (calendarLinkage == null)
            return null;

        User user = DBService.getFactory().getUserAccess().find(con, calendarLinkage.getUserId()); 
        if (user == null)
            throw new PartakeException(UserErrorCode.INVALID_NOTFOUND);

        Calendar calendar = CalendarUtil.createCalendarSkeleton();

        // We only consider the first 1000 entries of enrollments.
        List<Enrollment> enrollments = 
                DBService.getFactory().getEnrollmentAccess().findByUserId(con, user.getId(), 0, 1000); 
        for (Enrollment enrollment : enrollments) {
            Event event = DBService.getFactory().getEventAccess().find(con, enrollment.getEventId());
            if (event == null)
                continue;
            CalendarUtil.addToCalendar(calendar, event);
        }
        
        return calendar;
    }
}
