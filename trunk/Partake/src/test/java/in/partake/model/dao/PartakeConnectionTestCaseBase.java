package in.partake.model.dao;

import org.junit.Assert;
import org.junit.Test;

/**
 * Connection 関連のテストケースベース。
 */
public abstract class PartakeConnectionTestCaseBase extends AbstractDaoTestCaseBase {
    protected PartakeConnectionPool pool;
    
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
