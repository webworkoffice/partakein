package in.partake.model.dao;

import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dto.User;
import in.partake.model.fixture.impl.UserTestDataProvider;

import org.junit.Before;

public class UserAccessTest extends AbstractDaoTestCaseBase<IUserAccess, User, String> {
    
    @Before
    public void setup() throws DAOException {
        super.setup(factory.getUserAccess());
    }
    
    @Override
    protected User create(long pkNumber, String pkSalt, int objNumber) {
        return new UserTestDataProvider().create(pkNumber, pkSalt, objNumber);
    }    
}
