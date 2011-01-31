package in.partake.model.dao;

import java.util.Date;

import in.partake.model.dto.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class UserAccessTestCaseBase extends AbstractDaoTestCaseBase {
    
    private IUserAccess dao;
    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getUserAccess());
        dao = getFactory().getUserAccess();
    }
    
    @Test
    public void testToAddGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        try {
            User original = new User(null, 1, new Date(), "calendarId"); // TODO: calendarId はそのうちなくなる
            String userId;
            
            {
                con.beginTransaction();
                userId = dao.getFreshId(con);
                original.setId(userId);
                dao.addUser(con, original);
                con.commit();
            }
            
            User target;
            {
                con.beginTransaction();
                target = dao.getUser(con, userId);
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
    
    @Test
    public void testToUpdateLastLogin() throws Exception {
        PartakeConnection con = getPool().getConnection();
        try {
            Date now = new Date();
            User original = new User(null, 1, now, "calendarId"); // TODO: calendarId はそのうちなくなる
            String userId;
            
            {
                con.beginTransaction();
                userId = dao.getFreshId(con);
                original.setId(userId);
                dao.addUser(con, original);
                con.commit();
            }
            
            
            Date after = new Date(now.getTime() + 1000);
            {
                con.beginTransaction();
                User user = dao.getUser(con, userId);
                dao.updateLastLogin(con, user, after);
                con.commit();
            }
            
            User target;
            {
                con.beginTransaction();
                target = dao.getUser(con, userId);
                con.commit();
            }
            
            Assert.assertEquals(after, target.getLastLoginAt());            
        } finally {
            con.invalidate();
        }
    }
}
