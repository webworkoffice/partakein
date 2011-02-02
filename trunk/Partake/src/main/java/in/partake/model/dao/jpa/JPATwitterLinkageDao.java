package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.ITwitterLinkageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.TwitterLinkage;

class JPATwitterLinkageDao extends JPADao implements ITwitterLinkageAccess {

    @Override
    public void addTwitterLinkage(PartakeConnection con, TwitterLinkage embryo) throws DAOException {        
        EntityManager em = getEntityManager(con);
        em.persist(new TwitterLinkage(embryo));
    }

    @Override
    public TwitterLinkage getTwitterLinkageById(PartakeConnection con, int twitterId) throws DAOException {
        EntityManager em = getEntityManager(con);
        TwitterLinkage linkage = em.find(TwitterLinkage.class, twitterId);
        if (linkage == null) { return null; }
        return linkage.freeze();
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM TwitterLinkages");
        q.executeUpdate();
    }
}
