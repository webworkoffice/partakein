package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IUserSentMessageAccess;
import in.partake.model.dto.UserSentMessage;

import org.junit.Before;

public class UserSentMessageAccessTest extends AbstractDaoTestCaseBase<IUserSentMessageAccess, UserSentMessage, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getUserSentMessageAccess());
    }

    @Override
    protected UserSentMessage create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeApp.getTestService().getTestDataProviderSet().getUserSentMessageProvider().create(pkNumber, pkSalt, objNumber);
    }
}
