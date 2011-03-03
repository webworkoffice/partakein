package in.partake.model.dao.jpa;

import java.util.List;
import java.util.UUID;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.PartakeModel;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

abstract class JPADao<T extends PartakeModel<T>> {
    private static final Logger logger = Logger.getLogger(JPADao.class);

    protected EntityManager getEntityManager(PartakeConnection con) {
        return ((JPAConnection) con).getEntityManager();
    }
    
    protected String getFreshIdImpl(PartakeConnection con, Class<T> clazz) throws DAOException {
        JPAConnection jcon = (JPAConnection) con;

        String key = null;
        T obj = null;
        EntityManager em = jcon.getEntityManager();

        int tryCount = 0;
        do {
            key = UUID.randomUUID().toString();
            obj = em.find(clazz, key);
        } while (obj != null && tryCount++ < 5);

        // if the object is not null, this means that fresh id cannot be taken.
        if (obj != null) {
            logger.error("fresh id cannot be taken.");
            return null;
        } else {
            return key;
        }
    }
    
    protected T freeze(T t) {
        if (t == null) { return null; }
        return t.freeze();
    }
    
    @Deprecated
    protected void create(PartakeConnection con, T t, Class<T> clazz) {
        if (t == null) { throw new NullPointerException(); }
        if (t.getPrimaryKey() == null) { throw new NullPointerException(); }
        
        EntityManager em = getEntityManager(con);
        em.persist(t.copy());
    }

    @Deprecated
    protected void createWithoutPrimaryKey(PartakeConnection con, T t, Class<T> clazz) {
        if (t == null) { throw new NullPointerException(); }
        
        EntityManager em = getEntityManager(con);
        em.persist(t.copy());        
    }

    @Deprecated
    protected void createOrUpdate(PartakeConnection con, T t, Class<T> clazz) {
        putImpl(con, t, clazz);
    }

    protected void putImpl(PartakeConnection con, T t, Class<T> clazz) {
        if (t == null) { throw new NullPointerException(); }
        if (t.getPrimaryKey() == null) { throw new NullPointerException(); }
        
        EntityManager em = getEntityManager(con);
        T persisted = em.find(clazz, t.getPrimaryKey());
        if (persisted == null) {
            em.persist(t.copy());
        } else {
            em.detach(persisted);
            em.merge(t.copy());
        }
    }
    
    
    protected void removeImpl(PartakeConnection con, Object primaryKey, Class<T> clazz) {
        if (primaryKey == null) { throw new NullPointerException(); }
        
        EntityManager em = getEntityManager(con);
        T persisted = em.find(clazz, primaryKey);
        em.remove(persisted);
    }
    
    protected T findImpl(PartakeConnection con, Object primaryKey, Class<T> clazz) {
        if (primaryKey == null) { throw new NullPointerException(); }
        
        EntityManager em = getEntityManager(con);
        T persisted = em.find(clazz, primaryKey);
        return freeze(persisted);
    }

    protected DataIterator<T> getIteratorImpl(PartakeConnection con, String tableName, Class<T> clazz) {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM " + tableName + " t"); // TODO: tableName は tainted じゃないものを。
        
        @SuppressWarnings("unchecked")
        List<T> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<T>(em, list, clazz, false);        
    }
    
    protected void truncateImpl(PartakeConnection con, String tableName) {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM " + tableName);
        q.executeUpdate();   
    }
}
