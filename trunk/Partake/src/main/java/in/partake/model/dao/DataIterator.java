package in.partake.model.dao;


public abstract class DataIterator<T> {
    public abstract boolean hasNext() throws DAOException;    
    public abstract T next() throws DAOException;
    public abstract void remove() throws DAOException;
    public abstract void update(T t) throws DAOException;
}
