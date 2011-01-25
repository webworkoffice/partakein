package in.partake.model.dao;

import in.partake.model.dto.CalendarLinkage;


/**
 * @author shinyak
 */
public interface ICalendarLinkageAccess {
    public String getFreshCalendarId(PartakeConnection con) throws DAOException;
    
    public void addCalendarLinkageWithId(PartakeConnection con, CalendarLinkage embryo) throws DAOException;
    public CalendarLinkage getCalendarLinkageById(PartakeConnection con, String calendarId) throws DAOException;
    public void removeCalendarLinkageById(PartakeConnection con, String calendarId) throws DAOException;
    
    /** Use ONLY in unit tests.*/
    public abstract void truncate(PartakeConnection con) throws DAOException;
}
