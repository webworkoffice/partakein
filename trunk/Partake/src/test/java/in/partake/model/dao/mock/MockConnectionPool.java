package in.partake.model.dao.mock;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeConnectionPool;

public class MockConnectionPool extends PartakeConnectionPool {

    @Override
    public PartakeConnection getConnection() throws DAOException {
        return new MockConnection();
    }

    @Override
    public void releaseConnection(PartakeConnection connection) {
        // TODO Auto-generated method stub
        // do nothing?
    }

}
