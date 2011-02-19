package in.partake.model.dao;

import in.partake.model.dto.EventFeedLinkage;

import org.junit.Before;

public abstract class EventFeedAccessTestCaseBase extends AbstractDaoTestCaseBase<IEventFeedAccess, EventFeedLinkage, String> {    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getFeedAccess());
    }
    
    @Override
    protected EventFeedLinkage create(long pkNumber, String pkSalt, int objNumber) {
        return new EventFeedLinkage("feedId" + pkSalt + pkNumber, "eventId" + objNumber);
    }
}
