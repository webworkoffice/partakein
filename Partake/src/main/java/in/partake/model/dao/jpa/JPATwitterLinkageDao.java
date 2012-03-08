package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.ITwitterLinkageAccess;
import in.partake.model.dto.TwitterLinkage;

class JPATwitterLinkageDao extends JPADao<TwitterLinkage> implements ITwitterLinkageAccess {

    @Override
    public void put(PartakeConnection con, TwitterLinkage embryo) throws DAOException {
        putImpl(con, embryo, TwitterLinkage.class);
    }

    @Override
    public TwitterLinkage find(PartakeConnection con, String twitterId) throws DAOException {
        return findImpl(con, twitterId, TwitterLinkage.class);
    }
    
    @Override
    public void remove(PartakeConnection con, String twitterId) throws DAOException {
        removeImpl(con, twitterId, TwitterLinkage.class);
    }

    @Override
    public DataIterator<TwitterLinkage> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM TwitterLinkages t");
        
        @SuppressWarnings("unchecked")
        List<TwitterLinkage> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<TwitterLinkage>(em, list, TwitterLinkage.class, false);        
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM TwitterLinkages");
        q.executeUpdate();
    }
    
    @Override
    public long count(PartakeConnection con) throws DAOException {
        return countImpl(con, "TwitterLinkages");
    }

}
