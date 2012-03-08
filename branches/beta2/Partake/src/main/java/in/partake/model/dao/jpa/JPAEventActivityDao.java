package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dto.EventActivity;

class JPAEventActivityDao extends JPADao<EventActivity> implements IEventActivityAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, EventActivity.class);
    }

    @Override
    public EventActivity find(PartakeConnection con, String id) throws DAOException {
        return findImpl(con, id, EventActivity.class);
    }

    @Override
    public void put(PartakeConnection con, EventActivity embryo) throws DAOException {
        putImpl(con, embryo, EventActivity.class);
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        removeImpl(con, id, EventActivity.class);
    }

    @Override
    public DataIterator<EventActivity> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT activity FROM EventActivities activity");
        
        @SuppressWarnings("unchecked")
        List<EventActivity> events = q.getResultList();
        
        return new JPAPartakeModelDataIterator<EventActivity>(em, events, EventActivity.class, false);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM EventActivities");
        q.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EventActivity> findByEventId(PartakeConnection con, String eventId, int length) throws DAOException {
        EntityManager em = getEntityManager(con);
        
        Query q = em.createQuery("SELECT activity FROM EventActivities activity WHERE activity.eventId = :eventId ORDER BY activity.createdAt DESC");
        q.setMaxResults(length);
        q.setParameter("eventId", eventId);
        
        return q.getResultList();        
    }
    
    @Override
    public long count(PartakeConnection con) throws DAOException {
        return countImpl(con, "EventActivities");
    }

}
