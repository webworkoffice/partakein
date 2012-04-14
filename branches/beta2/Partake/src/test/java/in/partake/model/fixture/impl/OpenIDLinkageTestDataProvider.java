package in.partake.model.fixture.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dto.OpenIDLinkage;
import in.partake.model.fixture.TestDataProvider;

public class OpenIDLinkageTestDataProvider extends TestDataProvider<OpenIDLinkage> {

    @Override
    public OpenIDLinkage create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new OpenIDLinkage(uuid.toString(), "userId" + objNumber);
    }

    @Override
    public List<OpenIDLinkage> createSamples() {
        List<OpenIDLinkage> array = new ArrayList<OpenIDLinkage>();
        array.add(new OpenIDLinkage("identifier", "userId"));
        array.add(new OpenIDLinkage("identifier1", "userId"));
        array.add(new OpenIDLinkage("identifier", "userId1"));
        return array;
    }

    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IOpenIDLinkageAccess dao = daos.getOpenIDLinkageAccess();
        dao.truncate(con);

        dao.put(con, new OpenIDLinkage(DEFAULT_USER_OPENID_IDENTIFIER, DEFAULT_USER_ID));
        dao.put(con, new OpenIDLinkage(DEFAULT_USER_OPENID_ALTERNATIVE_IDENTIFIER, DEFAULT_USER_ID));
    }
}
