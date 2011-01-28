package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IFeedAccess;
import in.partake.model.dao.PartakeConnection;

class JPAFeedDao extends JPADao implements IFeedAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getFeedIdByEventId(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEventIdByFeedId(PartakeConnection con, String feedId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addFeedId(PartakeConnection con, String feedId, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM FeedLinkage");
        q.executeUpdate();
    }
}
