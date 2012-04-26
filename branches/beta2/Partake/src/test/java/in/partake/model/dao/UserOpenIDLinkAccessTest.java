package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IUserOpenIDLinkAccess;
import in.partake.model.dto.UserOpenIDLink;

import org.junit.Before;

public class UserOpenIDLinkAccessTest extends AbstractDaoTestCaseBase<IUserOpenIDLinkAccess, UserOpenIDLink, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getOpenIDLinkageAccess());
    }

    @Override
    protected UserOpenIDLink create(long pkNumber, String pkSalt, int objNumber) {
        return new UserOpenIDLink("id" + pkSalt + pkNumber, "userId" + objNumber);
    }
}
