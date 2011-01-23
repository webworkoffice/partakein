package in.partake.model.dao.jpa;

import org.junit.Assert;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;

import org.junit.Before;
import org.junit.Test;

public class JPAConnectionTest extends JPADaoTestBase {
    private JPAConnectionPool pool;
    
    @Before
    public void setUp() {
        pool = new JPAConnectionPool();
    }
    
    
    @Test
    public void testToConnectAndRelease() throws DAOException {
        PartakeConnection con = pool.getConnection();
        try {
            // do nothing.
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToRetain() throws DAOException {
        PartakeConnection con = pool.getConnection();
        try {
            con.retain();
            try {
                // do nothing
            } finally {
                con.invalidate();
            }
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToRetain2() throws DAOException {
        PartakeConnection con = pool.getConnection();
        con.invalidate();
        
        try {
            con.retain(); // should throw IllegalStateException.
            Assert.fail(); // NOT REACHED
        } catch (IllegalStateException e) {
            // ignore e.
        }
        
    }
}
