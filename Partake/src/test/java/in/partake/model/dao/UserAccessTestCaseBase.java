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
                dao.createUser(con, original);
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
            Assert.assertFalse(original.isFrozen());
            Assert.assertEquals(original, target);
            Assert.assertNotSame(original, target);
            
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToAddUpdateGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        try {
            User original = new User(null, 1, new Date(), "calendarId"); // TODO: calendarId はそのうちなくなる
            String userId;
            
            {
                con.beginTransaction();
                userId = dao.getFreshId(con);
                original.setId(userId);
                dao.createUser(con, original);
                con.commit();
            }
            
            {
                con.beginTransaction();
                User user = new User(dao.getUser(con, userId));
                user.setCalendarId("newCalendarId");
                dao.updateUser(con, user);
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.getUser(con, userId);
                con.commit();
            }
            
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToAddUpdate() throws Exception {
        PartakeConnection con = getPool().getConnection();
        try {
            String userId;
            {
                con.beginTransaction();
                userId = dao.getFreshId(con);
                User user1 = new User(userId, 1, new Date(), "calendarId");
                dao.createUser(con, user1);
                con.commit();
            }
            
            {
                con.beginTransaction();
                User user2 = new User(userId, 2, new Date(), "calendarId");
                dao.updateUser(con, user2);
                con.commit();
            }
            
            
        } finally {
            con.invalidate();
        }        
    }
    
    @Test
    public void testToAddUpdateInOneTransaction1() throws Exception {
        PartakeConnection con = getPool().getConnection();
        try {
            String userId;
            {
                con.beginTransaction();
                userId = dao.getFreshId(con);
                User user1 = new User(userId, 1, new Date(), "calendarId");
                dao.createUser(con, user1);

                user1.setTwitterId(2);
                dao.updateUser(con, user1);
                con.commit();
            }
        } finally {
            con.invalidate();
        }        
    }
    
    @Test
    public void testToAddUpdateInOneTransaction2() throws Exception {
        PartakeConnection con = getPool().getConnection();
        try {
            String userId;
            {
                con.beginTransaction();
                userId = dao.getFreshId(con);
                User user1 = new User(userId, 1, new Date(), "calendarId");
                dao.createUser(con, user1);

                User user2 = new User(userId, 2, new Date(), "calendarId");
                dao.updateUser(con, user2);
                con.commit();
            }
        } finally {
            con.invalidate();
        }        
    }
    
    @Test
    public void testToUpdateUpdateInOneTransaction1() throws Exception {
        PartakeConnection con = getPool().getConnection();
        try {
            String userId;
            {
                con.beginTransaction();
                userId = dao.getFreshId(con);
                User user1 = new User(userId, 1, new Date(), "calendarId");
                dao.updateUser(con, user1);

                User user2 = new User(userId, 2, new Date(), "calendarId");
                dao.updateUser(con, user2);
                con.commit();
            }
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
                dao.createUser(con, original);
                con.commit();
            }
            
            Date after = new Date(now.getTime() + 1000);
            {
                con.beginTransaction();
                dao.updateLastLogin(con, userId, after);
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
