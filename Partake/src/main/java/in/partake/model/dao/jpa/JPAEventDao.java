package in.partake.model.dao.jpa;

import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.KeyIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.model.dto.User;

class JPAEventDao extends JPADao implements IEventAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Event getEventById(PartakeConnection con, String id) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addEvent(PartakeConnection con, String eventId, Event embryo) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addEventAsDemo(PartakeConnection con, Event embryo) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateEvent(PartakeConnection con, Event original, Event embryo) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateEventRevision(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeEvent(PartakeConnection con, Event event) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean appendFeedId(PartakeConnection con, String eventId, String feedId) throws DAOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Event> getEventsByIds(PartakeConnection con, List<String> ids) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public KeyIterator getAllEventKeys(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Event> getEventsByOwner(PartakeConnection con, User owner) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Event> getEventsByOwner(PartakeConnection con, String userId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

}
