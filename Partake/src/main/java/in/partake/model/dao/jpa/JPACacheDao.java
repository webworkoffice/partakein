package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.ICacheAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.CacheData;

class JPACacheDao extends JPADao<CacheData> implements ICacheAccess {

    @Override
    public void addCache(PartakeConnection con, CacheData cacheData) throws DAOException {
        createOrUpdate(con, cacheData, CacheData.class);
    }

    @Override
    public void removeCache(PartakeConnection con, String cacheId) throws DAOException {
        remove(con, cacheId, CacheData.class);
    }

    @Override
    public CacheData getCache(PartakeConnection con, String cacheId) throws DAOException {
        return find(con, cacheId, CacheData.class);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM CacheData");
        q.executeUpdate();
    }
}
