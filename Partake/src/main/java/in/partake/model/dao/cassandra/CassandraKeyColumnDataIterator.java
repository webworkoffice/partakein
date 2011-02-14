package in.partake.model.dao.cassandra;

import java.util.NoSuchElementException;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;

// TODO: 未テスト
/**
 * 全ての key を iterate し、さらに column を iterate する。 
 */
public class CassandraKeyColumnDataIterator<T> extends DataIterator<T> {
    private CassandraConnection con;
    private CassandraTableDescription desc;
    private CassandraKeyIterator outer;
    private ColumnIterator inner;
    private ColumnOrSuperColumnKeyMapper<T> mapper;
    private String currentKey;
    
    public CassandraKeyColumnDataIterator(
            CassandraConnection con,
            CassandraTableDescription desc,
            ColumnOrSuperColumnKeyMapper<T> mapper) {
        this.con = con;
        this.desc = desc;
        this.outer = new CassandraKeyIterator(con, desc.keyspace, desc.prefix, desc.columnFamily, desc.readConsistency);
        this.inner = null;
        this.mapper = mapper;

    }
    
    @Override
    public boolean hasNext() throws DAOException {
        while (true) {
            if (inner == null) {
                if (!outer.hasNext()) { return false; }
                currentKey = outer.nextWithPrefix();
                inner = new ColumnIterator(con, desc.keyspace, currentKey, desc.columnFamily, false, desc.readConsistency, desc.writeConsistency);
            }

            if (inner.hasNext()) {
                return true;
            } else {
                inner = null;
            }
        }                
    }
    
    @Override
    public T next() throws DAOException {
        if (hasNext()) {
            return mapper.map(inner.next(), currentKey);
        } else {
            throw new NoSuchElementException();
        }
    }
    
    @Override
    public void remove() throws DAOException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    public void update(T t) throws DAOException ,UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
