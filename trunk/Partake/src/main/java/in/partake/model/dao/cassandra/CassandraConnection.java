package in.partake.model.dao.cassandra;

import org.apache.cassandra.thrift.Cassandra.Client;

import me.prettyprint.cassandra.service.CassandraClient;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.util.PDate;

class CassandraConnection extends PartakeConnection {
    private CassandraClient client;
    private long transactionAcquiredTime;
    
    public CassandraConnection(CassandraConnectionPool pool, String name, CassandraClient client, long time) {
        super(name, pool, time);
        this.client = client;
        this.transactionAcquiredTime = -1;
    }
    
    public Client getClient() {
        return client.getCassandra();
    }
    
    public CassandraClient getCassandraClient() {
        return client;
    }
    
    @Override
    public synchronized void invalidate() {
        this.transactionAcquiredTime = -1;
        super.invalidate();
    }

    @Override
    public long getAcquiredTime() {
        if (this.transactionAcquiredTime < 0) { 
            return super.getAcquiredTime();
        } else {
            return this.transactionAcquiredTime;
        }
    }
    
    @Override
    public void beginTransaction() throws DAOException {
        long now = PDate.getCurrentTime();
        this.transactionAcquiredTime = now;
    }
    
    @Override
    public void commit() throws DAOException {
        if (this.transactionAcquiredTime < 0) {
            throw new IllegalStateException("transaction is not acquired.");
        }
        this.transactionAcquiredTime = -1;
    }
    
    @Override
    public void rollback() throws DAOException {
        if (this.transactionAcquiredTime < 0) {
            throw new IllegalStateException("transaction is not acquired.");
        }
        this.transactionAcquiredTime = -1;
    }
}