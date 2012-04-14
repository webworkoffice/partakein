package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IUserReceivedMessageAccess;
import in.partake.model.dto.UserReceivedMessage;

import org.junit.Before;

public class UserReceivedMessageAccessTest extends AbstractDaoTestCaseBase<IUserReceivedMessageAccess, UserReceivedMessage, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getUserReceivedMessageAccess());
    }

    @Override
    protected UserReceivedMessage create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeApp.getTestService().getTestDataProviderSet().getUserReceivedMessageProvider().create(pkNumber, pkSalt, objNumber);
    }
}
