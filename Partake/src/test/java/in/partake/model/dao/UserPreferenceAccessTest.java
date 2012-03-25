package in.partake.model.dao;

import java.util.UUID;

import in.partake.model.dao.access.IUserPreferenceAccess;
import in.partake.model.dto.UserPreference;

import org.junit.Before;

public class UserPreferenceAccessTest extends AbstractDaoTestCaseBase<IUserPreferenceAccess, UserPreference, String> {
    @Before
    public void setup() throws DAOException {
        super.setup(factory.getUserPreferenceAccess());
    }

    @Override
    protected UserPreference create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("pref" + pkSalt).hashCode());
        return new UserPreference(uuid.toString(), (objNumber & 4) > 0, (objNumber & 2) > 0, (objNumber & 1) > 0);
    }
}
