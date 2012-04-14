package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dto.Message;

import org.junit.Before;

public class MessageAccessTest extends AbstractDaoTestCaseBase<IMessageAccess, Message, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getMessageAccess());
    }

    @Override
    protected Message create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeApp.getTestService().getTestDataProviderSet().getMessageProvider().create(pkNumber, pkSalt, objNumber);
    }
}
