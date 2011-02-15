package in.partake.model.dao;

import in.partake.model.dto.FeedLinkage;

import org.junit.Before;

public abstract class FeedAccessTestCaseBase extends AbstractDaoTestCaseBase<IFeedAccess, FeedLinkage, String> {    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getFeedAccess());
    }
    
    @Override
    protected FeedLinkage create(long pkNumber, String pkSalt, int objNumber) {
        return new FeedLinkage(pkSalt + pkNumber, "evetnId" + objNumber);
    }
}
