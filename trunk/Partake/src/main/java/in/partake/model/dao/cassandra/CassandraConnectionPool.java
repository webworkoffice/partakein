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
    
    // 同じ thread が複数の connection を取ると deadlock の可能性があるため、修正すること。
    private ThreadLocal<Integer> numAcquiredConnection;
    
    public CassandraConnectionPool() {
        this.numAcquiredConnection = new ThreadLocal<Integer>();
    }
    
    @Override
    public CassandraConnection getConnection() throws DAOException {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        
        String name = "unknown";
        if (stackTrace != null && stackTrace.length > 0) {
            name = stackTrace[0].getClassName() + "#" + stackTrace[0].getMethodName();
        }
        
        return getConnectionImpl(name);
    }
    
    private CassandraConnection getConnectionImpl(String name) throws DAOException {
        try {
            long now = new Date().getTime();
            CassandraClientPool pool = CassandraClientPoolFactory.INSTANCE.get();
            
            if (numAcquiredConnection.get() == null) {
                numAcquiredConnection.set(new Integer(1));
            } else {
                numAcquiredConnection.set(numAcquiredConnection.get() + 1);
            }
            if (numAcquiredConnection.get() > 1) {
                logger.warn(name + " : The same thread has taken multiple connections. This may cause a bug");
            }
            
            String host = PartakeProperties.get().getCassandraHost();
            int port = PartakeProperties.get().getCassandraPort();
            
            CassandraClient client = pool.borrowClient(host, port);
            
            logger.debug("borrowing... " + name + " : " + client.toString());
            return new CassandraConnection(this, name, client, now);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void releaseConnection(PartakeConnection connection) {
        int tenSeconds = 1000 * 10;
        Date now = new Date();
        
        if (connection.getAcquiredTime() + tenSeconds < now.getTime()) {
            logger.warn("connection [" + connection.getName() + "] have been acquired for " + (now.getTime() - connection.getAcquiredTime()) + " milliseconds.");
        }
        
        numAcquiredConnection.set(numAcquiredConnection.get() - 1);
        
        if (connection instanceof CassandraConnection) {
            releaseConnectionImpl((CassandraConnection) connection);
        } else {
            logger.warn("connection should be PartakeCassandraConnection. This may cause resource leak.");
        }
    }
    
    private void releaseConnectionImpl(CassandraConnection connection) {
        CassandraClientPool pool = CassandraClientPoolFactory.INSTANCE.get();
        try {
            logger.debug("releasing... " + connection.getClient().toString());
            pool.releaseClient(connection.getCassandraClient());
        } catch (Exception e) {
            logger.warn("releaseConnectionImpl failed by an exception.", e);
        }
    }
}
