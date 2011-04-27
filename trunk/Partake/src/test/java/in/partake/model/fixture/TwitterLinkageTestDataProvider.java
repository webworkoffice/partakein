package in.partake.model.fixture;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.ITwitterLinkageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.TwitterLinkage;

public class TwitterLinkageTestDataProvider {

    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        ITwitterLinkageAccess dao = factory.getTwitterLinkageAccess();        
        dao.truncate(con);
        
        // testUser という id の user がいることを保証する。
        dao.put(con, new TwitterLinkage(1, "testUser", "testUser", "accessToken", "accessTokenSecret", "http://www.example.com/", "testUser"));
    }

}
