package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;

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
    public void commit() {
        entityManager.getTransaction().commit();
    }

    @Override
    public void rollback() {
        entityManager.getTransaction().rollback();        
    }
        
    @Override
    public synchronized void invalidate() {
        // If the transaction is active, the transaction will be rolled back.
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }

        super.invalidate();
    }
    
    public EntityManager getEntityManager() {
        return entityManager;
    }
}