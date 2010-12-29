package in.partake.model.dao.mock;

import in.partake.model.dao.PartakeConnection;

public class MockConnection extends PartakeConnection {
    private int refCount;
    private long acquiredTime;
    private MockConnectionPool pool;
    
    public MockConnection(MockConnectionPool pool) {
        this.refCount = 1;
        this.acquiredTime = System.currentTimeMillis();
        this.pool = pool;
    }
    
    @Override
    public String getName() {
        return "<mock connection>";
    }

    @Override
    public long getAcquiredTime() {
        return acquiredTime;
    }

    @Override
    public synchronized void invalidate() {
        --refCount;
        if (refCount < 0) {
            throw new RuntimeException("invalidating too much!");
        } else if (refCount == 0) {
            pool.releaseConnection(this);
        }
    }

    @Override
    public synchronized void retain() {
        ++refCount;
    }

    @Override
    public void beginTransaction() {
    }

    @Override
    public void commit() {
    }

    @Override
    public void rollback() {
    }

}
