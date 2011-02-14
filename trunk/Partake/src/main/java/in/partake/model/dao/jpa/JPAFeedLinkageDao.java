package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IFeedAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.FeedLinkage;

class JPAFeedLinkageDao extends JPADao<FeedLinkage> implements IFeedAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, FeedLinkage.class);
    }

    @Override
    public void put(PartakeConnection con, FeedLinkage linkage) throws DAOException {
        putImpl(con, new FeedLinkage(linkage), FeedLinkage.class);
    }

    @Override
    public FeedLinkage find(PartakeConnection con, String feedId) throws DAOException {
        return findImpl(con, feedId, FeedLinkage.class);
    }
    
    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        removeImpl(con, key, FeedLinkage.class);
    }
    
    @Override
    public String findByEventId(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT feed FROM FeedLinkages AS feed WHERE feed.eventId = :eventId");
        q.setParameter("eventId", eventId);

        @SuppressWarnings("unchecked")
        List<FeedLinkage> feeds = q.getResultList();
        if (feeds.isEmpty()) { return null; }
        return feeds.get(0).getId();
    }

    @Override
    public DataIterator<FeedLinkage> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM FeedLinkages t");
        
        @SuppressWarnings("unchecked")
        List<FeedLinkage> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<FeedLinkage>(em, list, FeedLinkage.class, false);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM FeedLinkages");
        q.executeUpdate();
    }
}
