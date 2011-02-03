package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.ITwitterLinkageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.TwitterLinkage;

class JPATwitterLinkageDao extends JPADao<TwitterLinkage> implements ITwitterLinkageAccess {

    @Override
    public void addTwitterLinkage(PartakeConnection con, TwitterLinkage embryo) throws DAOException {
        createOrUpdate(con, embryo, TwitterLinkage.class);
    }

    @Override
    public TwitterLinkage getTwitterLinkageById(PartakeConnection con, int twitterId) throws DAOException {
        return find(con, twitterId, TwitterLinkage.class);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM TwitterLinkages");
        q.executeUpdate();
    }
}
