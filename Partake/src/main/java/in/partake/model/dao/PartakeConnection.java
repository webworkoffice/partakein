package in.partake.model.dao;

public abstract class PartakeConnection {
    public abstract String getName();
    public abstract long getAcquiredTime();
    public abstract void invalidate();
    public abstract void retain();

    public abstract void beginTransaction();
    public abstract void commit();
    public abstract void rollback();
    
}
