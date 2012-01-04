package in.partake.model.dao;

import in.partake.model.dto.UserPreference;

import org.junit.Before;

public class UserPreferenceAccessTest extends AbstractDaoTestCaseBase<IUserPreferenceAccess, UserPreference, String> {
    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getUserPreferenceAccess());
    }
    
    @Override
    protected UserPreference create(long pkNumber, String pkSalt, int objNumber) {
        return new UserPreference("userId" + pkSalt + pkNumber, (objNumber & 4) > 0, (objNumber & 2) > 0, (objNumber & 1) > 0);
    }

}
