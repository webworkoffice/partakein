package in.partake.base;

import in.partake.app.PartakeApp;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

public class TransactionTest {
    @BeforeClass
    public static void setUpOnce() throws Exception {
        PartakeApp.initialize("unittest");
    }

    @Test
    public void testWithDoingNothing() throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return null;
            }
        }.execute();
    }

    @Test
    public void testWithCommit() throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                con.commit();
                return null;
            }
        }.execute();
    }

    @Test
    public void testWithRollback() throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                con.rollback();
                return null;
            }
        }.execute();
    }

    @Test
    public void testWithBeginTransaction() throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                con.beginTransaction();
                return null;
            }
        }.execute();
    }

    @Test
    public void testWithException() throws Exception {
        DBAccess<Void> transaction = new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                Assert.assertEquals(1, PartakeApp.getDBService().getPool().getCurrentNumberOfConnectionForThisThread());
                throw new RuntimeException();
            }
        };

        Assert.assertEquals(0, PartakeApp.getDBService().getPool().getCurrentNumberOfConnectionForThisThread());
        transaction.execute();
        Assert.assertEquals(0, PartakeApp.getDBService().getPool().getCurrentNumberOfConnectionForThisThread());
    }
}
