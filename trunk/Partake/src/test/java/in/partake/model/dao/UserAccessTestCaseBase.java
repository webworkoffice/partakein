package in.partake.model.dao;

import java.util.Date;

import in.partake.model.dto.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class UserAccessTestCaseBase extends AbstractDaoTestCaseBase<IUserAccess, User, String> {
    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getUserAccess());
    }
    
    @Override
    protected User create(long pkNumber, String pkSalt, int objNumber) {
        return new User("userId" + pkSalt + pkNumber, 1, new Date(0), "calendarId" + objNumber);
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
                dao.put(con, original);
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
                target = dao.find(con, userId);
                con.commit();
            }
            
            Assert.assertEquals(after, target.getLastLoginAt());            
        } finally {
            con.invalidate();
        }
    }
}
