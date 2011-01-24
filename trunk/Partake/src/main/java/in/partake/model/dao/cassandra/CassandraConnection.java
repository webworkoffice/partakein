package in.partake.model.dao.cassandra;

import org.apache.cassandra.thrift.Cassandra.Client;

import me.prettyprint.cassandra.service.CassandraClient;
import in.partake.model.dao.PartakeConnection;

class CassandraConnection extends PartakeConnection {
    private CassandraClient client;
    
    public CassandraConnection(CassandraConnectionPool pool, String name, CassandraClient client, long time) {
        super(name, pool, time);
        this.client = client;
    }
    
    public Client getClient() {
        return client.getCassandra();
    }
    
    public CassandraClient getCassandraClient() {
        return client;
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