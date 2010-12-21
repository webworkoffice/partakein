package in.partake.model.dao.cassandra;

import org.apache.cassandra.thrift.Cassandra.Client;

import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.service.CassandraClientPoolFactory;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeModelFactory;

class PartakeCassandraConnection extends PartakeConnection {
	private PartakeModelFactory factory;
    private CassandraClient client;
    private long time;
    
    public PartakeCassandraConnection(PartakeModelFactory factory, CassandraClient client, long time) {
    	this.factory = factory;
        this.client = client;
        this.time = time;
    }
    
    public Client getClient() {
        return client.getCassandra();
    }
    
    public CassandraClient getCassandraClient() {
    	return client;
    }
    
    @Override
    public void invalidate() {
    	factory.releaseConnection(this);
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
