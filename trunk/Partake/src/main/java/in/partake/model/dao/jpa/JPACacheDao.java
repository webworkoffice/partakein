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
        if (cacheData.getId() == null) { throw new DAOException("id should be specified."); }
        EntityManager em = getEntityManager(con);
        em.persist(cacheData);
    }

    @Override
    public void removeCache(PartakeConnection con, String cacheId) throws DAOException {
        EntityManager em = getEntityManager(con);
        CacheData data = em.find(CacheData.class, cacheId);
        if (data != null) { em.remove(data); }
    }

    @Override
    public CacheData getCache(PartakeConnection con, String cacheId) throws DAOException {
        EntityManager em = getEntityManager(con);
        CacheData data = em.find(CacheData.class, cacheId);
        if (data != null) {
            return data.freeze();
        } else {
            return null;
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM CacheData");
        q.executeUpdate();
    }
}
