package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;

import java.sql.ResultSet;
import java.util.NoSuchElementException;

public class Postgres9EntityDataIterator<T> extends DataIterator<T> {
    
    private ResultSet resultSet;
    private T next;
    private T current;
    
    @Override
    public boolean hasNext() throws DAOException {
        if (next != null)
            return true;
        
        // TODO: create something.
//        if (resultSet.next()) {
//            next = fromEntity(T.class, )
//        }
        return false;
    }

    @Override
    public T next() throws DAOException {
        if (hasNext()) {
            current = next;
            next = null;
            return current;
        }

        assert next == null;
        current = null;
        throw new NoSuchElementException();
    }

    @Override
    public void remove() throws DAOException, UnsupportedOperationException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(T t) throws DAOException, UnsupportedOperationException {
        // TODO Auto-generated method stub
        
    }
    
    
}
