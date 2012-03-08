package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dto.EventFeedLinkage;

class JPAEventFeedLinkageDao extends JPADao<EventFeedLinkage> implements IEventFeedAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, EventFeedLinkage.class);
    }

    @Override
    public void put(PartakeConnection con, EventFeedLinkage linkage) throws DAOException {
        putImpl(con, new EventFeedLinkage(linkage), EventFeedLinkage.class);
    }

    @Override
    public EventFeedLinkage find(PartakeConnection con, String feedId) throws DAOException {
        return findImpl(con, feedId, EventFeedLinkage.class);
    }
    
    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        removeImpl(con, key, EventFeedLinkage.class);
    }
    
    @Override
    public String findByEventId(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT feed FROM EventFeedLinkages AS feed WHERE feed.eventId = :eventId");
        q.setParameter("eventId", eventId);

        @SuppressWarnings("unchecked")
        List<EventFeedLinkage> feeds = q.getResultList();
        if (feeds.isEmpty()) { return null; }
        return feeds.get(0).getId();
    }

    @Override
    public DataIterator<EventFeedLinkage> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM EventFeedLinkages t");
        
        @SuppressWarnings("unchecked")
        List<EventFeedLinkage> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<EventFeedLinkage>(em, list, EventFeedLinkage.class, false);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM EventFeedLinkages");
        q.executeUpdate();
    }
    
    @Override
    public long count(PartakeConnection con) throws DAOException {
        return countImpl(con, "EventFeedLinkages");
    }

}
