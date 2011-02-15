package in.partake.model.dao;

import in.partake.model.dto.UserPreference;
import in.partake.util.PDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class UserPreferenceTestCaseBase extends AbstractDaoTestCaseBase<IUserPreferenceAccess, UserPreference, String> {
    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getUserPreferenceAccess());
    }
    
    @Override
    protected UserPreference create(long pkNumber, String pkSalt, int objNumber) {
        return new UserPreference("userId" + pkSalt + pkNumber, (objNumber & 4) > 0, (objNumber & 2) > 0, (objNumber & 1) > 0);
    }
    
    @Test
    public void testToSetAndGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            UserPreference original = new UserPreference("userId", true, true, true);
            dao.put(con, original);
            UserPreference target = dao.find(con, "userId");
            
            con.commit();
            
            Assert.assertTrue(target.isFrozen());
            Assert.assertEquals(original, target);
        } finally {            
            con.invalidate();
        }
    }
    
    @Test
    public void testToSetGetSetGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        
        try {
            UserPreference original1 = new UserPreference("userId", true, true, true);
            {
                con.beginTransaction();
                dao.put(con, original1);
                con.commit();
            }
            
            PDate.waitForTick();
            
            {
                con.beginTransaction();
                UserPreference target = dao.find(con, "userId");
                Assert.assertTrue(target.isFrozen());
                Assert.assertEquals(original1, target);
                con.commit();
            }
            
            PDate.waitForTick();

            UserPreference original2 = new UserPreference("userId", true, true, false);
            {
                con.beginTransaction();
                dao.put(con, original2);
                con.commit();
            }
            
            PDate.waitForTick();

            {
                con.beginTransaction();
                UserPreference target = dao.find(con, "userId");
                Assert.assertTrue(target.isFrozen());
                Assert.assertEquals(original2, target);
                con.commit();
            }
        } finally {            
            con.invalidate();
        }
    }
    
    
    @Test(expected = NullPointerException.class)
    public void testToSetNotHavingId() throws Exception {
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            UserPreference original = new UserPreference(null, true, true, true);
            
            // should throw IllegalArgumentException
            dao.put(con, original);
            
            con.commit();
        } finally {            
            con.invalidate();
        }
    }

    @Test
    public void testToGetUnsetData() throws Exception {
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            String unusedUserId = "userId" + System.currentTimeMillis();
            
            UserPreference target = dao.find(con, unusedUserId);
            con.commit();

            Assert.assertNull(target);
        } finally {            
            con.invalidate();
        }
    }

}
