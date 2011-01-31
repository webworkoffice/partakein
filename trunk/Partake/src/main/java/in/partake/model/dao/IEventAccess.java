package in.partake.model.dao;

import in.partake.model.dto.Event;

import java.util.List;

public interface IEventAccess extends ITruncatable {

    // ----------------------------------------------------------------------
    // events
    
    // TODO: 他の dao は add と update を区別してないのになんでここは区別しているんだろう
    // ほかも add と update を区別するべき
    public abstract String getFreshId(PartakeConnection con) throws DAOException;
    
    public abstract void addEvent(PartakeConnection con, Event embryo) throws DAOException;
    public abstract Event getEvent(PartakeConnection con, String id) throws DAOException;
    public abstract void updateEvent(PartakeConnection con, Event embryo) throws DAOException;
    public abstract void removeEvent(PartakeConnection con, String eventId) throws DAOException;    
    
    // ----------------------------------------------------------------------
    // event utilities
    
    public abstract List<Event> getEventsByOwner(PartakeConnection con, String userId) throws DAOException;
    
    
    public abstract KeyIterator getAllEventKeys(PartakeConnection con) throws DAOException;    
}