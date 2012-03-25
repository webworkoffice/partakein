package in.partake.model.dao;

import java.util.UUID;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dto.EventFeedLinkage;

import org.junit.Before;

public class EventFeedAccessTest extends AbstractDaoTestCaseBase<IEventFeedAccess, EventFeedLinkage, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getEventFeedAccess());
    }

    @Override
    protected EventFeedLinkage create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("feed" + pkSalt).hashCode());
        return new EventFeedLinkage(uuid.toString(), "eventId" + objNumber);
    }
}
