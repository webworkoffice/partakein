package in.partake.model.dao.mock;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.util.PDate;

public class MockConnection extends PartakeConnection {
    public MockConnection(MockConnectionPool pool) {
        super("<mock connection>", pool, PDate.getCurrentTime());
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
