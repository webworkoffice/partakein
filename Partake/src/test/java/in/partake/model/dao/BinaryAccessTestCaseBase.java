package in.partake.model.dao;

import in.partake.model.dto.BinaryData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class BinaryAccessTestCaseBase extends AbstractDaoTestCaseBase {
    @Before
    public void setup() throws DAOException {
        // --- remove all data before starting test.
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            factory.getBinaryAccess().truncate(con);
            con.commit();
        } finally {            
            con.invalidate();
        }
    }

    @Test
    public void testToCreate() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            
            BinaryData data = new BinaryData("test", "test-type", new byte[] {1, 2, 3});
            factory.getBinaryAccess().addBinary(con, data);
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

            factory.getBinaryAccess().addBinary(con, original);
            BinaryData target = factory.getBinaryAccess().getBinary(con, "test");
            
            Assert.assertEquals(original.getId(), target.getId());
            Assert.assertEquals(original.getType(), target.getType());
            Assert.assertArrayEquals(original.getData(), target.getData());
            
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            con.invalidate();
        }
    }
    
    @Test(expected = DAOException.class)
    public void testToFailCreatingBinaryDataWithoutId() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            BinaryData data = new BinaryData(null, "test-type", new byte[] {1, 2, 3});
            factory.getBinaryAccess().addBinary(con, data);
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
                factory.getBinaryAccess().addBinary(con, data);
                con.commit();
            }
            {
                con.beginTransaction();
                factory.getBinaryAccess().removeBinary(con, "test");
                con.commit();
            }
            {
                con.beginTransaction();
                BinaryData data = factory.getBinaryAccess().getBinary(con, "test");
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
                factory.getBinaryAccess().addBinary(con, data);
                con.commit();
            }
            {
                con.beginTransaction();
                factory.getBinaryAccess().removeBinary(con, "invalid-id");
                con.commit();
            }
            {
                con.beginTransaction();
                BinaryData data = factory.getBinaryAccess().getBinary(con, "test");
                Assert.assertNotNull(data);
                con.commit();
            }
            {
                con.beginTransaction();
                BinaryData data = factory.getBinaryAccess().getBinary(con, "invalid-id");
                Assert.assertNull(data);
                con.commit();
            }
            
        } finally {
            con.invalidate();
        }    
    }
    
    @Test
    public void testToCreateDeleteCreateGet() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            {
                con.beginTransaction();
                BinaryData data = new BinaryData("test", "test-type", new byte[] {1, 2, 3});
                factory.getBinaryAccess().addBinary(con, data);
                con.commit();
            }
            {
                con.beginTransaction();
                factory.getBinaryAccess().removeBinary(con, "test");
                con.commit();
            }
            {
                con.beginTransaction();
                BinaryData data = new BinaryData("test", "test-type", new byte[] {1, 2, 3});
                factory.getBinaryAccess().addBinary(con, data);
                con.commit();
            }
            {
                con.beginTransaction();
                BinaryData data = factory.getBinaryAccess().getBinary(con, "test");
                Assert.assertNotNull(data);
                con.commit();
            }            
        } finally {
            con.invalidate();
        }
    }
}
