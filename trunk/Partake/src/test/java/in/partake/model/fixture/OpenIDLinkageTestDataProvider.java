package in.partake.model.fixture;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dto.OpenIDLinkage;

public class OpenIDLinkageTestDataProvider extends TestDataProvider {

    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        IOpenIDLinkageAccess dao = factory.getOpenIDLinkageAccess();
        dao.truncate(con);
        
        // testUser
        dao.put(con, new OpenIDLinkage("http://www.example.com/testuser", USER_ID1));
        dao.put(con, new OpenIDLinkage("http://www.example.com/testuser-alternative", USER_ID1));
        
        dao.put(con, new OpenIDLinkage("http://www.example.com/openid-remove-0", "openid-remove-0"));
        dao.put(con, new OpenIDLinkage("http://www.example.com/openid-remove-1", "openid-remove-1"));
        dao.put(con, new OpenIDLinkage("http://www.example.com/openid-remove-2", "openid-remove-2"));
        dao.put(con, new OpenIDLinkage("http://www.example.com/openid-remove-3", "openid-remove-3"));       
    }
}
