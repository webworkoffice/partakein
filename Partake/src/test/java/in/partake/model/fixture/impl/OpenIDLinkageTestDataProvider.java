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
        
        dao.put(con, new OpenIDLinkage(DEFAULT_USER_OPENID_IDENTIFIER, DEFAULT_USER_ID));
        dao.put(con, new OpenIDLinkage(DEFAULT_USER_OPENID_ALTERNATIVE_IDENTIFIER, DEFAULT_USER_ID));        
    }
}
