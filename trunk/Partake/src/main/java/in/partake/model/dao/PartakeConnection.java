package in.partake.model.dao;

public abstract class PartakeConnection {
    public abstract void beginTransaction();
    public abstract void commit();
    public abstract void rollback();
    
    public abstract long getAcquiredTime();
    public abstract void invalidate();
}
