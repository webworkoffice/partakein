package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.BinaryData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class JPABinaryDataDaoTest extends JPADaoTestBase {

    @Before
    public void setup() throws DAOException {
        // --- remove all data before starting test.
        JPAConnection con = (JPAConnection) getPool().getConnection();
        
        try {
            con.beginTransaction();
            EntityManager em = con.getEntityManager();
            
            Query q = em.createNativeQuery("truncate binarydata");
            q.executeUpdate();
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
            
            String id = "test";
            BinaryData data = new BinaryData("test-type", new byte[] {1, 2, 3});
            factory.getBinaryAccess().addBinaryWithId(con, id, data);
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
        String id = "test";
        BinaryData original = new BinaryData("test-type", new byte[] {1, 2, 3});        
        createAndGetInTransactionImpl(id, original);
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
        
        // should accept at least 20 MiB
        int N = 1024 * 1024 * 20;
        byte[] longArray = new byte[N];
        for (int i = 0; i < N; ++i) { 
            longArray[i] = (byte)(i % 100);
        }
        
        String id = "test";
        BinaryData original = new BinaryData(longString, longArray);        
        createAndGetInTransactionImpl(id, original);
    }

    private void createAndGetInTransactionImpl(String id, BinaryData original) throws DAOException, Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();

            factory.getBinaryAccess().addBinaryWithId(con, id, original);
            BinaryData target = factory.getBinaryAccess().getBinaryById(con, "test");
            
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
    
    
}
