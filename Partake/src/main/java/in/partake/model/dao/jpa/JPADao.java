package in.partake.model.dao.jpa;

import java.util.UUID;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.PartakeModel;

import javax.persistence.EntityManager;

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
    
    protected void create(PartakeConnection con, T t, Class<T> clazz) {
        if (t == null) { throw new NullPointerException(); }
        if (t.getPrimaryKey() == null) { throw new NullPointerException(); }
        
        EntityManager em = getEntityManager(con);
        em.persist(t.copy());
    }
    
    protected void createWithoutPrimaryKey(PartakeConnection con, T t, Class<T> clazz) {
        if (t == null) { throw new NullPointerException(); }
        
        EntityManager em = getEntityManager(con);
        em.persist(t.copy());        
    }
    
    protected void update(PartakeConnection con, T t, Class<T> clazz) {
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
    
    protected void createOrUpdate(PartakeConnection con, T t, Class<T> clazz) {
        update(con, t, clazz);
    }
    
    protected void remove(PartakeConnection con, Object primaryKey, Class<T> clazz) {
        if (primaryKey == null) { throw new NullPointerException(); }
        
        EntityManager em = getEntityManager(con);
        T persisted = em.find(clazz, primaryKey);
        em.remove(persisted);
    }
    
    protected T find(PartakeConnection con, Object primaryKey, Class<T> clazz) {
        if (primaryKey == null) { throw new NullPointerException(); }
        
        EntityManager em = getEntityManager(con);
        T persisted = em.find(clazz, primaryKey);
        return freeze(persisted);
    }
    
    
}
