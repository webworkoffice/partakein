package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IEventNotificationAccess;
import in.partake.model.dto.EventNotification;

import org.junit.Before;

public class EventNotificationAccessTest extends AbstractDaoTestCaseBase<IEventNotificationAccess, EventNotification, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getEventNotificationAccess());
    }

    @Override
    protected EventNotification create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeApp.getTestService().getTestDataProviderSet().getEventNotificationProvider().create(pkNumber, pkSalt, objNumber);
    }
}
