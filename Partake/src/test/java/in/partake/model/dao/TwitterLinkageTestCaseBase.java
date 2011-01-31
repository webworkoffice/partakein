package in.partake.model.dao;

import in.partake.model.dto.TwitterLinkage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class TwitterLinkageTestCaseBase extends AbstractDaoTestCaseBase {
    
    private ITwitterLinkageAccess dao;
    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getTwitterLinkageAccess());
        
        dao = getFactory().getTwitterLinkageAccess();
    }
    
    @Test
    public void testToAddGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        try {
            TwitterLinkage original = new TwitterLinkage(1, "screenName", "name", "accessToken", "accessTokenSecret", "profileImageURL", "userId");
            {
                con.beginTransaction();
                dao.addTwitterLinkage(con, original);
                con.commit();
            }
            
            TwitterLinkage target;
            {
                con.beginTransaction();
                target = dao.getTwitterLinkageById(con, 1);
                con.commit();
            }
            
            Assert.assertNotNull(target);
            Assert.assertTrue(target.isFrozen());
            Assert.assertEquals(original, target);
            Assert.assertNotSame(original, target);
        } finally {
            con.invalidate();
        }
    }
}
