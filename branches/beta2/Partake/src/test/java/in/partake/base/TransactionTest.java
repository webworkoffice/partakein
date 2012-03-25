package in.partake.base;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.resource.PartakeProperties;
import in.partake.service.DBService;
import in.partake.service.PartakeService;
import in.partake.service.TestDatabaseService;
import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

public class TransactionTest {
    @BeforeClass
    public static void setUpOnce() {
        PartakeProperties.get().reset("unittest");
        PartakeService.initialize();
        TestDatabaseService.initialize();
    }
    
    @Test
    public void testWithDoingNothing() throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
                return null;
            }
        }.execute();
    }
    
    @Test
    public void testWithCommit() throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
                con.commit();
                return null;
            }
        }.execute();
    }
    
    @Test
    public void testWithRollback() throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
                con.rollback();
                return null;
            }
        }.execute();
    }

    @Test
    public void testWithBeginTransaction() throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
                con.beginTransaction();
                return null;
            }
        }.execute();
    }

    @Test
    public void testWithException() throws Exception {
        Transaction<Void> transaction = new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
                Assert.assertEquals(1, DBService.getPool().getCurrentNumberOfConnectionForThisThread());
                throw new RuntimeException();
            }
        };
        
        Assert.assertEquals(0, DBService.getPool().getCurrentNumberOfConnectionForThisThread());
        try {
            transaction.execute();
        } catch (Exception e) {
        }
        
        Assert.assertEquals(0, DBService.getPool().getCurrentNumberOfConnectionForThisThread());
    }
}
