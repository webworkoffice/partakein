package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IUserTwitterLinkAccess;
import in.partake.model.dto.UserTwitterLink;
import org.junit.Before;

public class UserTwitterLinkAccessTest extends AbstractDaoTestCaseBase<IUserTwitterLinkAccess, UserTwitterLink, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getTwitterLinkageAccess());
    }

    @Override
    protected UserTwitterLink create(long pkNumber, String pkSalt, int objNumber) {
        return new UserTwitterLink(pkSalt + pkNumber, "screenName", "name", "accessToken", "accessTokenSecret", "profileImageURL", "userId" + objNumber);
    }
}
