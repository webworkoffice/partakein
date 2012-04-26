package in.partake.model.fixture.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserOpenIDLinkAccess;
import in.partake.model.dto.UserOpenIDLink;
import in.partake.model.fixture.TestDataProvider;

public class UserOpenIDLinkTestDataProvider extends TestDataProvider<UserOpenIDLink> {

    @Override
    public UserOpenIDLink create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new UserOpenIDLink(uuid.toString(), "userId" + objNumber);
    }

    @Override
    public List<UserOpenIDLink> createSamples() {
        List<UserOpenIDLink> array = new ArrayList<UserOpenIDLink>();
        array.add(new UserOpenIDLink("identifier", "userId"));
        array.add(new UserOpenIDLink("identifier1", "userId"));
        array.add(new UserOpenIDLink("identifier", "userId1"));
        return array;
    }

    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IUserOpenIDLinkAccess dao = daos.getOpenIDLinkageAccess();
        dao.truncate(con);

        dao.put(con, new UserOpenIDLink(DEFAULT_USER_OPENID_IDENTIFIER, DEFAULT_USER_ID));
        dao.put(con, new UserOpenIDLink(DEFAULT_USER_OPENID_ALTERNATIVE_IDENTIFIER, DEFAULT_USER_ID));
    }
}
