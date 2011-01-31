package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.KeyIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;

class JPAEventDao extends JPADao implements IEventAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, Event.class);
    }

    @Override
    public Event getEvent(PartakeConnection con, String id) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addEvent(PartakeConnection con, Event embryo) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateEvent(PartakeConnection con, Event embryo) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeEvent(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public KeyIterator getAllEventKeys(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Event> getEventsByOwner(PartakeConnection con, String userId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Events");
        q.executeUpdate();
    }
}
