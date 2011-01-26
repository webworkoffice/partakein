package in.partake.model.dao;

import org.junit.Assert;
import org.junit.Test;

/**
 * Connection 関連のテストケースベース。
 * extends して、BeforeClass, AfterClass で Connection などを設定のこと。
 */
public abstract class PartakeConnectionTestCaseBase extends AbstractDaoTestCaseBase {
    
    @Test
    public void testToConnectAndRelease() throws DAOException {
        PartakeConnection con = getPool().getConnection();
        try {
            // do nothing.
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToRetain() throws DAOException {
        PartakeConnection con = getPool().getConnection();
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
    
    @Test(expected = IllegalStateException.class)
    public void testToRetain2() throws DAOException {
        PartakeConnection con = getPool().getConnection();
        con.invalidate();
        
        con.retain(); // should throw IllegalStateException.
        Assert.fail(); // SHOULD NOT REACHED
    }
}
