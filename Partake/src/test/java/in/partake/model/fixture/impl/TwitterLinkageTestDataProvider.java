package in.partake.model.fixture.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.ITwitterLinkageAccess;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.fixture.TestDataProvider;

public class TwitterLinkageTestDataProvider extends TestDataProvider<TwitterLinkage> {

    @Override
    public TwitterLinkage create() {
        return create(0, "", 0);
    }
    
    @Override
    public TwitterLinkage create(long pkNumber, String pkSalt, int objNumber) {
        return new TwitterLinkage("twitterId" + pkNumber + pkSalt, "screenName", "name", "accessToken",
                "accessTokenSecret", "profileImageURL", "userId" + objNumber);
    }
    
    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        ITwitterLinkageAccess dao = factory.getTwitterLinkageAccess();        
        dao.truncate(con);
        
        // testUser という id の user がいることを保証する。
        dao.put(con, new TwitterLinkage(1, "testUser", "testUser", "accessToken", "accessTokenSecret", "http://www.example.com/", USER_ID1));

        dao.put(con, new TwitterLinkage(1000, "openid-remove-0", "openid-remove-0", "accessToken", "accessTokenSecret", "http://www.example.com/", "openid-remove-0"));
        dao.put(con, new TwitterLinkage(1001, "openid-remove-1", "openid-remove-1", "accessToken", "accessTokenSecret", "http://www.example.com/", "openid-remove-1"));
        dao.put(con, new TwitterLinkage(1002, "openid-remove-2", "openid-remove-2", "accessToken", "accessTokenSecret", "http://www.example.com/", "openid-remove-2"));
    }

}
