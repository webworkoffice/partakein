package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IFeedAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.FeedLinkage;

class JPAFeedLinkageDao extends JPADao<FeedLinkage> implements IFeedAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, FeedLinkage.class);
    }

    @Override
    public void addFeedId(PartakeConnection con, String feedId, String eventId) throws DAOException {
        createOrUpdate(con, new FeedLinkage(feedId, eventId), FeedLinkage.class);
    }

    @Override
    public String getEventIdByFeedId(PartakeConnection con, String feedId) throws DAOException {
        FeedLinkage linkage = find(con, feedId, FeedLinkage.class);
        if (linkage == null) { return null; }
        return linkage.getEventId();
    }
    
    @Override
    public String getFeedIdByEventId(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT feed FROM FeedLinkages AS feed WHERE feed.eventId = :eventId");
        q.setParameter("eventId", eventId);

        @SuppressWarnings("unchecked")
        List<FeedLinkage> feeds = q.getResultList();
        if (feeds.isEmpty()) { return null; }
        return feeds.get(0).getId();
    }



    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM FeedLinkages");
        q.executeUpdate();
    }
}
