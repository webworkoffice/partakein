package in.partake.model.dao;

import in.partake.model.dto.TwitterLinkage;
import org.junit.Before;

public abstract class TwitterLinkageTestCaseBase extends AbstractDaoTestCaseBase<ITwitterLinkageAccess, TwitterLinkage, String> {    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getTwitterLinkageAccess());
    }
    
    @Override
    protected TwitterLinkage create(long pkNumber, String pkSalt, int objNumber) {
        return new TwitterLinkage(pkSalt + pkNumber, "screenName", "name", "accessToken", "accessTokenSecret", "profileImageURL", "userId" + objNumber);
    }
    
}
