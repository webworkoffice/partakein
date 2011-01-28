package in.partake.model.dao;

import java.util.Date;

import in.partake.model.dto.CacheData;
import in.partake.util.PDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class CacheAccessTestCaseBase extends AbstractDaoTestCaseBase {
    
    private Date oneHourAfter() {
        return new Date(new Date().getTime() + 3600 * 1000);
    }
    
    private Date oneHourBefore() {
        return new Date(new Date().getTime() - 3600 * 1000);
    }
    
    @Before
    public void setup() throws DAOException {
        super.setup();
        
        // --- remove all data before starting test.
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            factory.getCacheAccess().truncate(con);
            con.commit();
        } finally {            
            con.invalidate();
        }
    }

    @Test
    public void testToAdd() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            CacheData cacheData = new CacheData("test", new byte[] { 1, 2, 3 }, oneHourAfter());
            factory.getCacheAccess().addCache(con, cacheData);
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
        CacheData original = new CacheData("test", new byte[] { 1, 2, 3 }, oneHourAfter());
        createAndGetInTransactionImpl(original);
    }

    @Test
    public void testToCreateAndGetInTransactionLong() throws Exception {
        // should accept at least 10 MiB
        int N = 1024 * 1024 * 10;
        byte[] longArray = new byte[N];
        for (int i = 0; i < N; ++i) { 
            longArray[i] = (byte)(i % 100);
        }
        
        CacheData original = new CacheData("test", longArray, oneHourAfter());        
        createAndGetInTransactionImpl(original);
    }

    private void createAndGetInTransactionImpl(CacheData original) throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();

            factory.getCacheAccess().addCache(con, original);
            CacheData target = factory.getCacheAccess().getCache(con, "test");
            
            Assert.assertEquals(original.getId(), target.getId());
            Assert.assertArrayEquals(original.getData(), target.getData());
            Assert.assertTrue(target.isFrozen());
            
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
            CacheData data = new CacheData(null, new byte[] {1, 2, 3}, oneHourAfter());
            factory.getCacheAccess().addCache(con, data);
            con.commit();
        } finally {
            con.invalidate();
        }
    }
    
    /** null should be returned if the expired data is retrieved. */
    public void testToCreateAndGetExpiredData() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            {
                con.beginTransaction();
                CacheData data = new CacheData("test", new byte[] {1, 2, 3}, oneHourBefore());
                factory.getCacheAccess().addCache(con, data);
                con.commit();
            }
            
            {
                con.beginTransaction();
                CacheData data = factory.getCacheAccess().getCache(con, "test");
                Assert.assertNull(data);
                con.commit();
            }
        } finally {
            con.invalidate();
        }
    }
    
    /** null should be returned if the expired data is retrieved. */
    public void testToCreateAndGetExpiredDataUsingPDate() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            long now = oneHourAfter().getTime();
                        
            {
                con.beginTransaction();
                CacheData data = new CacheData("test", new byte[] {1, 2, 3}, new Date(now + 1));
                factory.getCacheAccess().addCache(con, data);
                con.commit();
            }
            
            // In this time, data can be retrieved.
            PDate.setCurrentDate(new PDate(now - 1));
            {
                con.beginTransaction();
                CacheData data = factory.getCacheAccess().getCache(con, "test");
                Assert.assertNotNull(data);
                con.commit();
            }
            
            // In this time, data is expired.
            PDate.setCurrentDate(new PDate(now + 1));
            {
                con.beginTransaction();
                CacheData data = factory.getCacheAccess().getCache(con, "test");
                Assert.assertNull(data);
                con.commit();
            }
            
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToRemoveCacheData() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            {
                con.beginTransaction();
                CacheData data = new CacheData("test", new byte[] { 1, 2, 3 }, oneHourAfter());
                factory.getCacheAccess().addCache(con, data);
                con.commit();
            }
            {
                con.beginTransaction();
                factory.getCacheAccess().removeCache(con, "test");
                con.commit();
            }
            {
                con.beginTransaction();
                CacheData data = factory.getCacheAccess().getCache(con, "test");
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
                CacheData data = new CacheData("test", new byte[] { 1, 2, 3 }, oneHourAfter());
                factory.getCacheAccess().addCache(con, data);
                con.commit();
            }
            {
                con.beginTransaction();
                factory.getCacheAccess().removeCache(con, "invalid-id");
                con.commit();
            }
            {
                con.beginTransaction();
                CacheData data = factory.getCacheAccess().getCache(con, "test");
                Assert.assertNotNull(data);
                con.commit();
            }
            {
                con.beginTransaction();
                CacheData data = factory.getCacheAccess().getCache(con, "invalid-id");
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
        
        long now = new Date().getTime();
        try {
            PDate.setCurrentDate(new PDate(now));
            {
                con.beginTransaction();
                CacheData data = new CacheData("test", new byte[] { 1, 2, 3 }, oneHourAfter());
                factory.getCacheAccess().addCache(con, data);
                con.commit();
            }
            PDate.setCurrentDate(new PDate(now + 10));
            {
                con.beginTransaction();
                factory.getCacheAccess().removeCache(con, "test");
                con.commit();
            }
            PDate.setCurrentDate(new PDate(now + 20));
            {
                con.beginTransaction();
                CacheData data = new CacheData("test", new byte[] { 1, 2, 3 }, oneHourAfter());
                factory.getCacheAccess().addCache(con, data);
                con.commit();
            }
            PDate.setCurrentDate(new PDate(now + 30));
            {
                con.beginTransaction();
                CacheData data = factory.getCacheAccess().getCache(con, "test");
                Assert.assertNotNull(data);
                con.commit();
            }
        } finally {
            con.invalidate();
        }
        PDate.resetCurrentDate();
    }
}
