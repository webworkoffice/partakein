package in.partake.model.fixture;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.User;

import java.util.Date;

public class UserTestDataProvider {

    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        
        IUserAccess dao = factory.getUserAccess();
        dao.truncate(con);
        
        // testUser という id の user がいることを保証する。
        dao.put(con, new User("testUser", 1, new Date(), null)); 
    }
}
