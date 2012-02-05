package in.partake.model.dao;

import java.util.Date;
import java.util.UUID;

import in.partake.base.TimeUtil;
import in.partake.model.dao.access.ICacheAccess;
import in.partake.model.dto.CacheData;
import in.partake.model.fixture.impl.CacheTestDataProvider;

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
        
        String id = UUID.randomUUID().toString();
        try {
            {
                con.beginTransaction();
                CacheData data = new CacheData(id, new byte[] {1, 2, 3}, oneHourBefore());
                factory.getCacheAccess().put(con, data);
                con.commit();
            }
            
            {
                con.beginTransaction();
                CacheData data = factory.getCacheAccess().find(con, id);
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
        
        String id = UUID.randomUUID().toString();
        try {
            long now = oneHourAfter().getTime();
                        
            {
                con.beginTransaction();
                CacheData data = new CacheData(id, new byte[] {1, 2, 3}, new Date(now + 1));
                factory.getCacheAccess().put(con, data);
                con.commit();
            }
            
            // In this time, data can be retrieved.
            TimeUtil.setCurrentDate(new Date(now - 1));
            {
                con.beginTransaction();
                CacheData data = factory.getCacheAccess().find(con, id);
                Assert.assertNotNull(data);
                con.commit();
            }
            
            // In this time, data is expired.
            TimeUtil.setCurrentDate(new Date(now + 1));
            {
                con.beginTransaction();
                CacheData data = factory.getCacheAccess().find(con, id);
                Assert.assertNull(data);
                con.commit();
            }
            
        } finally {
            con.invalidate();
        }
    }    
}
