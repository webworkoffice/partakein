package in.partake.model.dao;

import in.partake.model.dto.CalendarLinkage;


/**
 * @author shinyak
 */
public interface ICalendarLinkageAccess extends ITruncatable {
    public String getFreshCalendarId(PartakeConnection con) throws DAOException;
    
    public void addCalendarLinkage(PartakeConnection con, CalendarLinkage embryo) throws DAOException;
    public CalendarLinkage getCalendarLinkage(PartakeConnection con, String id) throws DAOException;
    public CalendarLinkage getCalendarLinkageByUserId(PartakeConnection con, String userId) throws DAOException;
    public void removeCalendarLinkage(PartakeConnection con, String id) throws DAOException;
}
