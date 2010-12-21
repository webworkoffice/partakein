package in.partake.model.dao;

public abstract class PartakeConnectionPool {
    public abstract PartakeConnection getConnection(String name) throws DAOException;
    public abstract void releaseConnection(PartakeConnection connection);
}
