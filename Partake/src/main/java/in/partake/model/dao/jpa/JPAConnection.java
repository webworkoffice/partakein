package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;

public class JPAConnection extends PartakeConnection { 
    private EntityManager entityManager;

    public JPAConnection(JPAConnectionPool pool, EntityManager entityManager, String name, long time) {
        super(name, pool, time);
        this.entityManager = entityManager;
    }

    @Override
    public void beginTransaction() {
        entityManager.getTransaction().begin();
    }

    @Override
    public void commit() throws IllegalStateException, RollbackException {
        entityManager.getTransaction().commit();
    }

    @Override
    public void rollback() throws IllegalStateException, PersistenceException {
        entityManager.getTransaction().rollback();
    }
    
    @Override
    public boolean isInTransaction() throws DAOException {
        return entityManager.getTransaction().isActive();
    }

    @Override
    public synchronized void invalidate() throws PersistenceException {
        try {
            // If the transaction is active, the transaction will be rolled back.
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } finally {
            super.invalidate();
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}