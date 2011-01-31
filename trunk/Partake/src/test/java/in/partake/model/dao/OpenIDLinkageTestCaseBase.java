package in.partake.model.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class OpenIDLinkageTestCaseBase extends AbstractDaoTestCaseBase {
    private IOpenIDLinkageAccess dao;
    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getOpenIDLinkageAccess());
        
        dao = getFactory().getOpenIDLinkageAccess();
    }
    
    @Test
    public void testToAddGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        try {
            {
                con.beginTransaction();
                dao.addOpenID(con, "identifier", "userId");
                con.commit();
            }
            
            String userId;
            {
                con.beginTransaction();
                userId = dao.getUserId(con, "identifier");
                con.commit();
            }
            
            Assert.assertEquals("userId", userId);
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToAddDeleteGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        try {
            {
                con.beginTransaction();
                dao.addOpenID(con, "identifier", "userId");
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.removeOpenID(con, "identifier");
                con.commit();
            }
            
            String userId;
            {
                con.beginTransaction();
                userId = dao.getUserId(con, "identifier");
                con.commit();
            }
            
            Assert.assertNull(userId);
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToAddDeleteAddGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        try {
            {
                con.beginTransaction();
                dao.addOpenID(con, "identifier", "userId");
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.removeOpenID(con, "identifier");
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.addOpenID(con, "identifier", "userId");
                con.commit();
            }
            
            String userId;
            {
                con.beginTransaction();
                userId = dao.getUserId(con, "identifier");
                con.commit();
            }
            
            Assert.assertEquals("userId", userId);
        } finally {
            con.invalidate();
        }
    }
}
