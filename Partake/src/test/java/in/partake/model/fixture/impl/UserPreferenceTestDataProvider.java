package in.partake.model.fixture.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IUserPreferenceAccess;
import in.partake.model.dto.UserPreference;
import in.partake.model.fixture.TestDataProvider;

import java.util.UUID;

public class UserPreferenceTestDataProvider extends TestDataProvider<UserPreference> {

    @Override
    public UserPreference create() {
        return new UserPreference();
    }
    
    @Override
    public UserPreference create(long pkNumber, String pkSalt, int objNumber) {
        UUID id = new UUID(pkNumber, pkSalt.hashCode()); 

        boolean profilePublic = (objNumber & 0x1) != 0;
        boolean receivingTwitterMessage = (objNumber & 0x10) != 0;
        boolean tweetingAttendanceAutomatically = (objNumber & 0x100) != 0;
        return new UserPreference(id.toString(), profilePublic, receivingTwitterMessage, tweetingAttendanceAutomatically);
    }
    
    @Override
    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        IUserPreferenceAccess dao = factory.getUserPreferenceAccess();
        dao.truncate(con);
        
        dao.put(con, new UserPreference(DEFAULT_USER_ID, true, true, false));
    }
}
