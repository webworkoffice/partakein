package in.partake.model.dao;

import in.partake.model.dto.OpenIDLinkage;

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
                dao.put(con, new OpenIDLinkage("identifier", "userId"));
                con.commit();
            }
            
            String userId;
            {
                con.beginTransaction();
                userId = dao.find(con, "identifier").getUserId();
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
                dao.put(con, new OpenIDLinkage("identifier", "userId"));
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.remove(con, "identifier");
                con.commit();
            }
            
            OpenIDLinkage linkage;
            {
                con.beginTransaction();
                linkage = dao.find(con, "identifier");
                con.commit();
            }
            
            Assert.assertNull(linkage);
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
                dao.put(con, new OpenIDLinkage("identifier", "userId"));
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.remove(con, "identifier");
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.put(con, new OpenIDLinkage("identifier", "userId"));
                con.commit();
            }
            
            String userId;
            {
                con.beginTransaction();
                userId = dao.find(con, "identifier").getUserId();
                con.commit();
            }
            
            Assert.assertEquals("userId", userId);
        } finally {
            con.invalidate();
        }
    }
}
