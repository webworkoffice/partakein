package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import in.partake.model.dao.PartakeConnection;

public class JPAConnection extends PartakeConnection { 
    private static final Logger logger = Logger.getLogger(JPAConnection.class);
    
    
    
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getAcquiredTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void invalidate() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void retain() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void beginTransaction() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void commit() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void rollback() {
        // TODO Auto-generated method stub
        
    }
        
    public EntityManager getEntityManager() {
        throw new RuntimeException("not implemented yet.");
    }
}
