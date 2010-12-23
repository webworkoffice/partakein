package in.partake.model.dao.cassandra;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.log4j.Logger;

import me.prettyprint.cassandra.service.CassandraClient;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeModelFactory;

class PartakeCassandraConnection extends PartakeConnection {
    private static final Logger logger = Logger.getLogger(PartakeCassandraConnection.class);

	private PartakeModelFactory factory;
	private String name;
    private CassandraClient client;
    private long time;
    private int refCount;
    
    public PartakeCassandraConnection(PartakeModelFactory factory, String name, CassandraClient client, long time) {
    	this.factory = factory;
    	this.name = name;
        this.client = client;
        this.time = time;
        this.refCount = 1;
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (refCount > 0) {
            logger.error("RESOURCE LEAK! : Connection [" + name + "] has been retained yet.");
            
            // call invalidate.
            while (refCount > 0) {
                invalidate();
            }
        }
            
        super.finalize();
    }
    
    public Client getClient() {
        return client.getCassandra();
    }
    
    public CassandraClient getCassandraClient() {
    	return client;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public synchronized void retain() {
        ++refCount;
    }
    
    @Override
    public synchronized void invalidate() {
        --refCount;
        
        if (refCount == 0) {
            factory.releaseConnection(this);
        } else if (refCount < 0) {
            logger.error("invalidate() called too much!");
        }
    }
    
    @Override
    public long getAcquiredTime() {
        return time;
    }
    
    @Override
    public void beginTransaction() {
        // ignored.
    }
    
    @Override
    public void commit() {
        // ignored.
    }
    
    @Override
    public void rollback() {
        // ignored.
    }
}
