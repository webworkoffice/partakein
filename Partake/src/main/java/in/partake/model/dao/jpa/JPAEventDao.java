package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;

class JPAEventDao extends JPADao implements IEventAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, Event.class);
    }

    @Override
    public Event getEvent(PartakeConnection con, String id) throws DAOException {
        EntityManager em = getEntityManager(con);
        Event event = em.find(Event.class, id);
        return freeze(event);
    }

    @Override
    public void addEvent(PartakeConnection con, Event embryo) throws DAOException {
        if (embryo.getId() == null) { throw new NullPointerException(); }
        
        EntityManager em = getEntityManager(con);
        em.persist(new Event(embryo));        
    }

    @Override
    public void updateEvent(PartakeConnection con, Event embryo) throws DAOException {
        EntityManager em = getEntityManager(con);
        em.merge(embryo);
    }

    @Override
    public void removeEvent(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Event event = em.find(Event.class, eventId);
        if (event != null) { em.remove(event); }
    }

    @Override
    public DataIterator<Event> getAllEventIterators(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT event FROM Events event");
        
        @SuppressWarnings("unchecked")
        List<Event> events = q.getResultList();
        
        return new JPAPartakeModelDataIterator<Event>(em, events, false);
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
