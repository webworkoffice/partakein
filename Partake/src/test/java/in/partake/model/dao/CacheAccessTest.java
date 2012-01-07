package in.partake.model.dao;

import java.util.Date;

import in.partake.model.dao.access.ICacheAccess;
import in.partake.model.dto.CacheData;
import in.partake.model.fixture.CacheTestDataProvider;
import in.partake.util.PDate;

import org.junit.Assert;
import org.junit.Before;

public class CacheAccessTest extends AbstractDaoTestCaseBase<ICacheAccess, CacheData, String> {
    private CacheTestDataProvider provider = new CacheTestDataProvider();
    
    private Date oneHourAfter() {
        return new Date(new Date().getTime() + 3600 * 1000);
    }
    
    private Date oneHourBefore() {
        return new Date(new Date().getTime() - 3600 * 1000);
    }
    
    @Override
    protected CacheData create(long pkNumber, String pkSalt, int objNumber) {
        return provider.create(pkNumber, pkSalt, objNumber);
    }
        
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getCacheAccess());
    }

    /** null should be returned if the expired data is retrieved. */
    public void testToPutFindExpiredData() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            {
                con.beginTransaction();
                CacheData data = new CacheData("test", new byte[] {1, 2, 3}, oneHourBefore());
                factory.getCacheAccess().put(con, data);
                con.commit();
            }
            
            {
                con.beginTransaction();
                CacheData data = factory.getCacheAccess().find(con, "test");
                Assert.assertNull(data);
                con.commit();
            }
        } finally {
            con.invalidate();
        }
    }
    
    /** null should be returned if the expired data is retrieved. */
    public void testToPutFindExpiredDataUsingPDate() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            long now = oneHourAfter().getTime();
                        
            {
                con.beginTransaction();
                CacheData data = new CacheData("test", new byte[] {1, 2, 3}, new Date(now + 1));
                factory.getCacheAccess().put(con, data);
                con.commit();
            }
            
            // In this time, data can be retrieved.
            PDate.setCurrentDate(new PDate(now - 1));
            {
                con.beginTransaction();
                CacheData data = factory.getCacheAccess().find(con, "test");
                Assert.assertNotNull(data);
                con.commit();
            }
            
            // In this time, data is expired.
            PDate.setCurrentDate(new PDate(now + 1));
            {
                con.beginTransaction();
                CacheData data = factory.getCacheAccess().find(con, "test");
                Assert.assertNull(data);
                con.commit();
            }
            
        } finally {
            con.invalidate();
        }
    }    
}
