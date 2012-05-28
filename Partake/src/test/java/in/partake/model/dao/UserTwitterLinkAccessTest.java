package in.partake.model.dao;

import java.util.UUID;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IUserTwitterLinkAccess;
import in.partake.model.dto.UserTwitterLink;
import org.junit.Before;

public class UserTwitterLinkAccessTest extends AbstractDaoTestCaseBase<IUserTwitterLinkAccess, UserTwitterLink, UUID> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getTwitterLinkageAccess());
    }

    @Override
    protected UserTwitterLink create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new UserTwitterLink(uuid, objNumber, "userId" + objNumber, "screenName", "name", "accessToken", "accessTokenSecret", "profileImageURL");
    }
}
