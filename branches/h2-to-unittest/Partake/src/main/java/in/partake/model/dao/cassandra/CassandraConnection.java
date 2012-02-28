package in.partake.model.dao.cassandra;

import org.apache.cassandra.thrift.Cassandra.Client;

import me.prettyprint.cassandra.service.CassandraClient;
import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;

class CassandraConnection extends PartakeConnection {
    private CassandraClient client;
    private long transactionAcquiredTime;
    private long lastTransactionAcquiredTime;
    
    public CassandraConnection(CassandraConnectionPool pool, String name, CassandraClient client, long time) {
        super(name, pool, time);
        this.client = client;
        this.transactionAcquiredTime = -1;
        this.lastTransactionAcquiredTime = -1;
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
        long now = TimeUtil.getCurrentTime();
        this.transactionAcquiredTime = now;
        if (transactionAcquiredTime == lastTransactionAcquiredTime) {
            transactionAcquiredTime = lastTransactionAcquiredTime + 1;
        }
        
        lastTransactionAcquiredTime = transactionAcquiredTime;
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