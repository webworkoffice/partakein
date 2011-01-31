package in.partake.model.dao;

import in.partake.model.dto.Event;
import in.partake.model.dto.User;

import java.util.List;

public interface IEventAccess extends ITruncatable {

    // ----------------------------------------------------------------------
    // events
    
    // TODO: 他の dao は add と update を区別してないのになんでここは区別しているんだろう
    public abstract String getFreshId(PartakeConnection con) throws DAOException;
    public abstract Event getEventById(PartakeConnection con, String id) throws DAOException;
    public abstract void addEvent(PartakeConnection con, Event embryo) throws DAOException;
    public abstract void updateEvent(PartakeConnection con, Event original, Event embryo) throws DAOException;
    public abstract void removeEvent(PartakeConnection con, String eventId) throws DAOException;    

    // TODO: こいつらはあとで消す
    // public abstract void addEventAsDemo(PartakeConnection con, Event embryo) throws DAOException;
    public abstract void updateEventRevision(PartakeConnection con, String eventId) throws DAOException;

    // feed
    // TODO: これここにあるべきかー？ FeedDAO に移動
    public abstract boolean appendFeedId(PartakeConnection con, String eventId, String feedId) throws DAOException;    
    
    // ----------------------------------------------------------------------
    // event utilities
    
    public abstract List<Event> getEventsByOwner(PartakeConnection con, String userId) throws DAOException;
    public abstract KeyIterator getAllEventKeys(PartakeConnection con) throws DAOException;
    
    // TODO: こいつら後で消す
    @Deprecated
    public abstract List<Event> getEventsByIds(PartakeConnection con, List<String> ids) throws DAOException;
    @Deprecated
    public abstract List<Event> getEventsByOwner(PartakeConnection con, User owner) throws DAOException;
}