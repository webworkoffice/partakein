package in.partake.model.dao.mock;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;

public class MockConnection extends PartakeConnection {
    public MockConnection(MockConnectionPool pool) {
        super("<mock connection>", pool, TimeUtil.getCurrentTime());
    }

    @Override
    public void beginTransaction() throws DAOException {
    }

    @Override
    public void commit() throws DAOException {
    }

    @Override
    public void rollback() throws DAOException {
    }

}
