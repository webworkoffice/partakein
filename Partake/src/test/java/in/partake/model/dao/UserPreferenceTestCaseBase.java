package in.partake.model.dao;

import in.partake.model.dto.UserPreference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class UserPreferenceTestCaseBase extends AbstractDaoTestCaseBase {
    @Before
    public void setup() throws DAOException {
        super.setup();
        
        // --- remove all data before starting test.
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            factory.getUserPreferenceAccess().truncate(con);
            con.commit();
        } finally {            
            con.invalidate();
        }
    }
    
    @Test
    public void testToSetAndGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            UserPreference original = new UserPreference("userId", true, true, true);
            factory.getUserPreferenceAccess().setPreference(con, original);
            UserPreference target = factory.getUserPreferenceAccess().getPreference(con, "userId");
            
            con.commit();
            
            Assert.assertTrue(target.isFrozen());
            Assert.assertEquals(original, target);
        } finally {            
            con.invalidate();
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testToSetNotHavingId() throws Exception {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            UserPreference original = new UserPreference(null, true, true, true);
            
            // should throw IllegalArgumentException
            factory.getUserPreferenceAccess().setPreference(con, original);
            
            con.commit();
        } finally {            
            con.invalidate();
        }
    }

    @Test
    public void testToGetUnsetData() throws Exception {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            UserPreference target = factory.getUserPreferenceAccess().getPreference(con, "userId");
            con.commit();

            Assert.assertNull(target);
        } finally {            
            con.invalidate();
        }
    }

}
