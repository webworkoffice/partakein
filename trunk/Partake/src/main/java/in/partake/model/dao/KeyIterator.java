package in.partake.model.dao;


public abstract class KeyIterator {
    public abstract boolean hasNext() throws DAOException;
    public abstract String next() throws DAOException;
    public abstract String nextWithPrefix() throws DAOException;
}
