package in.partake.model.dao.jpa;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.ICalendarLinkageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.CalendarLinkage;

class JPACalendarLinkageDao extends JPADao implements ICalendarLinkageAccess {

    @Override
    public String getFreshCalendarId(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public void addCalendarLinkageWithId(PartakeConnection con, String calendarId, CalendarLinkage embryo) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public CalendarLinkage getCalendarLinkageById(PartakeConnection con, String calendarId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public void removeCalendarLinkageById(PartakeConnection con, String calendarId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        
    }
}
