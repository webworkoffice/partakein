package in.partake.model.dao;

public abstract class PartakeConnectionPool {
    public abstract PartakeConnection getConnection() throws DAOException;
    public abstract void releaseConnection(PartakeConnection connection);
}
