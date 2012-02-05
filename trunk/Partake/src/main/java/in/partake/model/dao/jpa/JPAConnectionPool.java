package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeConnectionPool;
import in.partake.resource.PartakeProperties;

public class JPAConnectionPool extends PartakeConnectionPool {
    private static final Logger logger = Logger.getLogger(JPAConnectionPool.class);
    
    private EntityManagerFactory entityManagerFactory;
    
    public JPAConnectionPool() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(PartakeProperties.get().getJPAPersistenceUnitName()); 
    }
    
    @Override
    protected JPAConnection getConnectionImpl(String name) throws DAOException {
        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            return new JPAConnection(this, em, name, TimeUtil.getCurrentTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
        
    @Override
    protected void releaseConnectionImpl(PartakeConnection connection) {
        if (connection instanceof JPAConnection) {
            ((JPAConnection) connection).getEntityManager().close();
        } else {
            logger.warn("connection is not instanceof JPAConnection");
        }
    }
    
    @Override
    public void willDestroy() {
        entityManagerFactory.close();
    }
}
