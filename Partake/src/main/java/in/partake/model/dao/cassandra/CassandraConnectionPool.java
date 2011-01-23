package in.partake.model.dao.cassandra;

import java.util.Date;

import org.apache.log4j.Logger;

import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.service.CassandraClientPoolFactory;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeConnectionPool;
import in.partake.resource.PartakeProperties;

public class CassandraConnectionPool extends PartakeConnectionPool {
    private static final Logger logger = Logger.getLogger(CassandraConnectionPool.class);
    
    public CassandraConnectionPool() {
    }
    
    @Override
    protected CassandraConnection getConnectionImpl(String name) throws DAOException {
        try {
            long now = new Date().getTime();
            CassandraClientPool pool = CassandraClientPoolFactory.INSTANCE.get();
            
            String host = PartakeProperties.get().getCassandraHost();
            int port = PartakeProperties.get().getCassandraPort();
            
            CassandraClient client = pool.borrowClient(host, port);
            
            return new CassandraConnection(this, name, client, now);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    protected void releaseConnectionImpl(PartakeConnection connection) {
        if (connection instanceof CassandraConnection) {
            CassandraClientPool pool = CassandraClientPoolFactory.INSTANCE.get();
            try {
                pool.releaseClient(((CassandraConnection) connection).getCassandraClient());
            } catch (Exception e) {
                logger.warn("releaseConnectionImpl failed by an exception.", e);
            }
        } else {
            logger.warn("connection is not instanceof CassandraConnection");
        }
    }
}
