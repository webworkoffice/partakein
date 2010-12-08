package in.partake.model.dao;

import in.partake.model.dto.Event;
import in.partake.model.dto.User;

import java.util.List;

public interface IEventAccess {

    // ----------------------------------------------------------------------
    // events
    public abstract String getFreshId(PartakeConnection con) throws DAOException;
    public abstract Event getEventById(PartakeConnection con, String id) throws DAOException;
    public abstract void addEvent(PartakeConnection con, String eventId, Event embryo) throws DAOException;
    public abstract void addEventAsDemo(PartakeConnection con, Event embryo) throws DAOException;
    public abstract void updateEvent(PartakeConnection con, Event original, Event embryo) throws DAOException;
    public abstract void removeEvent(PartakeConnection con, Event event) throws DAOException;    

    // feed
    // TODO: これここにあるべきかー？
    public abstract boolean appendFeedId(PartakeConnection con, String eventId, String feedId) throws DAOException;    
    
    // ----------------------------------------------------------------------
    // event utilities
    public abstract List<Event> getEventsByIds(PartakeConnection con, List<String> ids) throws DAOException;
    public abstract KeyIterator getAllEventKeys(PartakeDAOFactory factory) throws DAOException;
    public abstract List<Event> getEventsByOwner(PartakeConnection con, User owner) throws DAOException;
    
}