package in.partake.service;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.CalendarLinkage;
import in.partake.model.dto.User;

public final class CalendarService extends PartakeService {
    private static CalendarService instance = new CalendarService();
    
    public static CalendarService get() {
        return instance;
    }
    
    private CalendarService() {
        // do nothing for now.
    }
    
    /**
     * revoke the current calendar and re-generate calendar id.
     */
    public void revokeCalendar(User user) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            
            // If the calendar already exists, remove it first.
            String calendarId = user.getCalendarId();
            if (calendarId != null) {
                factory.getCalendarAccess().remove(con, calendarId);
            }
    
            // 新しくカレンダー id を作成して保存
            calendarId = factory.getCalendarAccess().getFreshId(con);
            CalendarLinkage embryo = new CalendarLinkage(calendarId, user.getId());
            factory.getCalendarAccess().put(con, embryo);
            
            con.commit();
        } finally {
            con.invalidate();
        }
    }
}
