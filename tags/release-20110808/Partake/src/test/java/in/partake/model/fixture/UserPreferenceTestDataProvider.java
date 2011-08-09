package in.partake.model.fixture;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IUserPreferenceAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.UserPreference;

public class UserPreferenceTestDataProvider extends TestDataProvider {

    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        IUserPreferenceAccess dao = factory.getUserPreferenceAccess();
        dao.truncate(con);
        
        dao.put(con, new UserPreference(USER_ID1, true, true, true));
        dao.put(con, new UserPreference(USER_ID2, true, true, true));
        dao.put(con, new UserPreference(USER_ID3, true, true, true));
    }
}
