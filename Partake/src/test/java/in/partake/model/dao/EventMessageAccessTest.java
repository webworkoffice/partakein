package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IEventMessageAccess;
import in.partake.model.dto.EventMessage;

import org.junit.Before;

public class EventMessageAccessTest extends AbstractDaoTestCaseBase<IEventMessageAccess, EventMessage, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getEventMessageAccess());
    }

    @Override
    protected EventMessage create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeApp.getTestService().getTestDataProviderSet().getEventMessageProvider().create(pkNumber, pkSalt, objNumber);
    }
}
