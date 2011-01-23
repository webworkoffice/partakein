package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import in.partake.model.dao.PartakeConnection;

public class JPAConnection extends PartakeConnection { 
    private static final Logger logger = Logger.getLogger(JPAConnection.class);
    
    private JPAConnectionPool pool;
    private String name;
    private EntityManager entityManager;
    private int refCount;
    private long time;
    
    public JPAConnection(JPAConnectionPool pool, EntityManager entityManager, String name, long time) {
        this.pool = pool;
        this.name = name;
        this.entityManager = entityManager;
        this.refCount = 1;
        this.time = time;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getAcquiredTime() {
        return time;
    }

    @Override
    public synchronized void invalidate() {
        --refCount;
        
        if (refCount == 0) {
            pool.releaseConnection(this);
        } else if (refCount < 0) {
            logger.error("invalidate() called too much!");
            throw new IllegalStateException("invalidate() called too much");
        }
    }

    @Override
    public synchronized void retain() {
        if (refCount <= 0) {
            throw new IllegalStateException("refCount should not less than or equal to 0 to retain the connection.");
        }
        
        ++refCount;
    }

    @Override
    public void beginTransaction() {
        entityManager.getTransaction().begin();
    }

    @Override
    public void commit() {
        entityManager.getTransaction().commit();
    }

    @Override
    public void rollback() {
        entityManager.getTransaction().rollback();        
    }
        
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
