package in.partake.model.dao;

import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dto.EventFeedLinkage;

import org.junit.Before;

public class EventFeedAccessTest extends AbstractDaoTestCaseBase<IEventFeedAccess, EventFeedLinkage, String> {    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getEventFeedAccess());
    }
    
    @Override
    protected EventFeedLinkage create(long pkNumber, String pkSalt, int objNumber) {
        return new EventFeedLinkage("feedId" + pkSalt + pkNumber, "eventId" + objNumber);
    }
}
