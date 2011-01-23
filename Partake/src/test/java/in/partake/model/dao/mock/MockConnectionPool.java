package in.partake.model.dao.mock;

import java.util.HashSet;
import java.util.Set;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeConnectionPool;

public class MockConnectionPool extends PartakeConnectionPool {
    private Set<PartakeConnection> connections;
    
    public MockConnectionPool() {
        connections = new HashSet<PartakeConnection>();
    }
    
    @Override
    protected PartakeConnection getConnectionImpl(String name) throws DAOException {
        PartakeConnection con = new MockConnection(this);
        connections.add(con);
        return con;        
    }
    
    @Override
    protected void releaseConnectionImpl(PartakeConnection connection) {
        connections.remove(connection);
    }
    
    public boolean areAllConnectionsReleased() {
        return connections.isEmpty();
    }
}
