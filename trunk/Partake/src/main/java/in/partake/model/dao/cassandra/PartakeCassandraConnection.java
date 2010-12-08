package in.partake.model.dao.cassandra;

import org.apache.cassandra.thrift.Cassandra.Client;

import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.service.CassandraClientPoolFactory;
import in.partake.model.dao.PartakeConnection;

class PartakeCassandraConnection extends PartakeConnection {
    private CassandraClient client;
    private long time;
    
    public PartakeCassandraConnection(CassandraClient client, long time) {
        this.client = client;
        this.time = time;
    }
    
    public Client getClient() {
        return client.getCassandra();
    }
    
    @Override
    public void invalidate() {
        CassandraClientPool pool = CassandraClientPoolFactory.INSTANCE.get();
        try {
            System.out.println("releasing... " + client.toString());
            pool.releaseClient(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // pool.invalidateClient(client);
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
