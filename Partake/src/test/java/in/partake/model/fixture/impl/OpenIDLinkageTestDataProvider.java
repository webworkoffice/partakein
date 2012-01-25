package in.partake.model.fixture.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dto.OpenIDLinkage;
import in.partake.model.fixture.TestDataProvider;

public class OpenIDLinkageTestDataProvider extends TestDataProvider<OpenIDLinkage> {

    @Override
    public OpenIDLinkage create() {
        throw new RuntimeException("Not implemented yet");
    }
    
    @Override
    public OpenIDLinkage create(long pkNumber, String pkSalt, int objNumber) {
        throw new RuntimeException("Not implemented yet");
    }
    
    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        IOpenIDLinkageAccess dao = factory.getOpenIDLinkageAccess();
        dao.truncate(con);
        
        // testUser
        dao.put(con, new OpenIDLinkage("http://www.example.com/testuser", USER_ID1));
        dao.put(con, new OpenIDLinkage("http://www.example.com/testuser-alternative", USER_ID1));
        
        dao.put(con, new OpenIDLinkage("http://www.example.com/openid-remove-0", EVENT_REMOVE_ID0));
        dao.put(con, new OpenIDLinkage("http://www.example.com/openid-remove-1", EVENT_REMOVE_ID1));
        dao.put(con, new OpenIDLinkage("http://www.example.com/openid-remove-2", EVENT_REMOVE_ID2));
        dao.put(con, new OpenIDLinkage("http://www.example.com/openid-remove-3", EVENT_REMOVE_ID3));       
    }
}
