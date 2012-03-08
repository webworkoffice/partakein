package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.ICacheAccess;
import in.partake.model.dto.CacheData;

class JPACacheDao extends JPADao<CacheData> implements ICacheAccess {

    @Override
    public void put(PartakeConnection con, CacheData cacheData) throws DAOException {
        putImpl(con, cacheData, CacheData.class);
    }

    @Override
    public CacheData find(PartakeConnection con, String cacheId) throws DAOException {
        return findImpl(con, cacheId, CacheData.class);
    }

    @Override
    public void remove(PartakeConnection con, String cacheId) throws DAOException {
        removeImpl(con, cacheId, CacheData.class);
    }
    
    @Override
    public DataIterator<CacheData> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM CacheData t");
        
        @SuppressWarnings("unchecked")
        List<CacheData> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<CacheData>(em, list, CacheData.class, false);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM CacheData");
        q.executeUpdate();
    }
    
    @Override
    public long count(PartakeConnection con) throws DAOException {
        return countImpl(con, "CacheData");
    }

}
