package in.partake.model.dao.jpa;

import in.partake.model.dao.PartakeConnection;

import javax.persistence.EntityManager;

abstract class JPADao {

    protected EntityManager getEntityManager(PartakeConnection con) {
        return ((JPAConnection) con).getEntityManager();
    }
}
