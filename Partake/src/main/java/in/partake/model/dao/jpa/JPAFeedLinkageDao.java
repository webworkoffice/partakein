package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IFeedAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.FeedLinkage;

class JPAFeedLinkageDao extends JPADao implements IFeedAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, FeedLinkage.class);
    }

    @Override
    public void addFeedId(PartakeConnection con, String feedId, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        em.persist(new FeedLinkage(feedId, eventId));
    }

    
    @Override
    public String getFeedIdByEventId(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT feed FROM FeedLinkages AS feed WHERE feed.eventId = :eventId");
        q.setParameter("eventId", eventId);
        
        FeedLinkage feed = (FeedLinkage) q.getSingleResult();
        if (feed == null) { return null; }
        return feed.getId();
    }

    @Override
    public String getEventIdByFeedId(PartakeConnection con, String feedId) throws DAOException {
        EntityManager em = getEntityManager(con);
        FeedLinkage feed = em.find(FeedLinkage.class, feedId);

        if (feed == null) { return null; }
        return feed.getEventId();
    }


    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM FeedLinkages");
        q.executeUpdate();
    }
}
