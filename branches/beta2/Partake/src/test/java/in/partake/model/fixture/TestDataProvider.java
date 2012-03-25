package in.partake.model.fixture;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;

public abstract class TestDataProvider<T> implements TestDataProviderConstants {
    // TODO: Name should be more descriptive.

    @Deprecated
    public abstract T create();
    public abstract T create(long pkNumber, String pkSalt, int objNumber);
    public abstract void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException;
}
