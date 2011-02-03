package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;

class JPAEventDao extends JPADao<Event> implements IEventAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, Event.class);
    }

    @Override
    public Event getEvent(PartakeConnection con, String id) throws DAOException {
        return find(con, id, Event.class);
    }

    @Override
    public void addEvent(PartakeConnection con, Event embryo) throws DAOException {
        createOrUpdate(con, embryo, Event.class);
    }

    @Override
    public void updateEvent(PartakeConnection con, Event embryo) throws DAOException {
        update(con, embryo, Event.class);
    }

    @Override
    public void removeEvent(PartakeConnection con, String eventId) throws DAOException {
        remove(con, eventId, Event.class);
    }

    @Override
    public DataIterator<Event> getAllEventIterators(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT event FROM Events event");
        
        @SuppressWarnings("unchecked")
        List<Event> events = q.getResultList();
        
        // TOOD: need copy? or freeze?
        
        return new JPAPartakeModelDataIterator<Event>(em, events, Event.class, false);
    }

    @Override
    public List<Event> getEventsByOwner(PartakeConnection con, String userId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT event FROM Events event WHERE event.ownerId = :userId");
        q.setParameter("userId", userId);
        
        @SuppressWarnings("unchecked")
        List<Event> events = (List<Event>) q.getResultList();
        for (Event event : events) { event.freeze(); }
        
        return events;
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Events");
        q.executeUpdate();
    }
}
