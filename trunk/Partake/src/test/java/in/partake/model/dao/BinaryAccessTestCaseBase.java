package in.partake.model.dao;

import in.partake.model.dto.BinaryData;
import in.partake.util.PDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class BinaryAccessTestCaseBase extends AbstractDaoTestCaseBase {
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getBinaryAccess()); 
    }

    @Test
    public void testToCreate() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            
            BinaryData data = new BinaryData("test", "test-type", new byte[] {1, 2, 3});
            factory.getBinaryAccess().put(con, data);
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToCreateAndGetInTransactionShort() throws Exception {
        BinaryData original = new BinaryData("test", "test-type", new byte[] {1, 2, 3});        
        createAndGetInTransactionImpl(original);
    }

    @Test
    public void testToCreateAndGetInTransactionLong() throws Exception {
        String longString;
        {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 10000; ++i) {
                builder.append('a');
            }
            longString = builder.toString();
        }
        
        // should accept at least 10 MiB
        int N = 1024 * 1024 * 10;
        byte[] longArray = new byte[N];
        for (int i = 0; i < N; ++i) { 
            longArray[i] = (byte)(i % 100);
        }
        
        BinaryData original = new BinaryData("test", longString, longArray);        
        createAndGetInTransactionImpl(original);
    }

    private void createAndGetInTransactionImpl(BinaryData original) throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();

            factory.getBinaryAccess().put(con, original);
            BinaryData target = factory.getBinaryAccess().find(con, "test");
            
            Assert.assertEquals(original, target);
            Assert.assertNotSame(original, target);
            Assert.assertTrue(target.isFrozen());
            Assert.assertFalse(original.isFrozen());
            
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            con.invalidate();
        }
    }
    
    @Test(expected = NullPointerException.class)
    public void testToFailCreatingBinaryDataWithoutId() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            BinaryData data = new BinaryData(null, "test-type", new byte[] {1, 2, 3});
            factory.getBinaryAccess().put(con, data);
            con.commit();
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToRemoveBinaryData() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            {
                con.beginTransaction();
                BinaryData data = new BinaryData("test", "test-type", new byte[] {1, 2, 3});
                factory.getBinaryAccess().put(con, data);
                con.commit();
            }
            {
                con.beginTransaction();
                factory.getBinaryAccess().remove(con, "test");
                con.commit();
            }
            {
                con.beginTransaction();
                BinaryData data = factory.getBinaryAccess().find(con, "test");
                Assert.assertNull(data);
                con.commit();
            }
            
        } finally {
            con.invalidate();
        }        
    }
    
    @Test
    public void testToIgnoreInvalIdWhenRemovingData() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            {
                con.beginTransaction();
                BinaryData data = new BinaryData("test", "test-type", new byte[] {1, 2, 3});
                factory.getBinaryAccess().put(con, data);
                con.commit();
            }
            PDate.waitForTick();
            {
                con.beginTransaction();
                factory.getBinaryAccess().remove(con, "invalid-id");
                con.commit();
            }
            PDate.waitForTick();
            {
                con.beginTransaction();
                BinaryData data = factory.getBinaryAccess().find(con, "test");
                Assert.assertNotNull(data);
                con.commit();
            }
            PDate.waitForTick();
            {
                con.beginTransaction();
                BinaryData data = factory.getBinaryAccess().find(con, "invalid-id");
                Assert.assertNull(data);
                con.commit();
            }
            
        } finally {
            con.invalidate();
        }    
    }
    
    @Test
    public void testToAddRemoveAddGet() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            {
                con.beginTransaction();
                BinaryData data = new BinaryData("test", "test-type", new byte[] {1, 2, 3});
                factory.getBinaryAccess().put(con, data);
                con.commit();
            }
            {
                con.beginTransaction();
                factory.getBinaryAccess().remove(con, "test");
                con.commit();
            }
            {
                con.beginTransaction();
                BinaryData data = new BinaryData("test", "test-type", new byte[] {1, 2, 3});
                factory.getBinaryAccess().put(con, data);
                con.commit();
            }
            {
                con.beginTransaction();
                BinaryData data = factory.getBinaryAccess().find(con, "test");
                Assert.assertNotNull(data);
                con.commit();
            }            
        } finally {
            con.invalidate();
        }
    }
}
