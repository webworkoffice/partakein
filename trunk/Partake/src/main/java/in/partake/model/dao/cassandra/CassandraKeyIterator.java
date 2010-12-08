package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.KeyIterator;
import in.partake.model.dao.PartakeConnection;

import java.util.List;
import java.util.NoSuchElementException;

import me.prettyprint.cassandra.service.CassandraClient;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.KeySlice;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;

/**
 * Key の範囲を動く。
 * @author shinyak
 *
 */
class CassandraKeyIterator extends KeyIterator {
    private String keyspace;
    private ColumnParent columnParent; // TODO: ColumnFamily をとるべき
    private ConsistencyLevel readConsistencyLevel;

    private String keyPrefix;
    private String nextStartKey;    // null if all elements are retrieved.
    private String rangeEndKey; 
    
    private CassandraDAOFactory factory;
    
    private int pos;
    private List<KeySlice> keySlice;
    
    // package private
    // TODO: temporarily public. should be package private.
    public CassandraKeyIterator(CassandraDAOFactory factory, String keyspace, String keyPrefix, String columnFamily, ConsistencyLevel readConsistencyLevel) {
        this.keyspace = keyspace;
        this.columnParent = new ColumnParent(columnFamily);        
        this.readConsistencyLevel = readConsistencyLevel;
        
        this.keyPrefix = keyPrefix;
        this.nextStartKey = keyPrefix;
        
        if (keyPrefix.isEmpty()) {
            this.rangeEndKey = "";
        } else {
            char[] t = keyPrefix.toCharArray();
            t[t.length - 1]++;
            this.rangeEndKey = new String(t);
        }
            
        this.factory = factory;
        
        this.pos = 0;
        this.keySlice = null;
    }

    public boolean hasNext() throws DAOException {
        if (keySlice != null && pos < keySlice.size()) { return true; }
        if (nextStartKey == null) { return false; }

        PartakeCassandraConnection con = (PartakeCassandraConnection) factory.getConnection();
        try {
            Client cassandra = con.getClient();
            
            SlicePredicate slicePredicate = new SlicePredicate();
            SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 100);
            slicePredicate.setSlice_range(sliceRange);

            KeyRange keyRange = new KeyRange();
            keyRange.start_key = nextStartKey;
            keyRange.end_key = rangeEndKey;
            
            keySlice = cassandra.get_range_slices(keyspace, columnParent, slicePredicate, keyRange, readConsistencyLevel);
            
            if (keySlice == null || keySlice.isEmpty()) {
                pos = 0;
                nextStartKey = null;
                return false;
            } else {
                pos = 0;
                
                String lastKey = keySlice.get(keySlice.size() - 1).getKey();
                nextStartKey = lastKey + "\0";                
                
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DAOException(e);
        } finally {
            con.invalidate();
        }
    }
    
    public String next() throws DAOException {
        return nextWithPrefix().substring(this.keyPrefix.length());
    }
    
    public String nextWithPrefix() throws DAOException {
        if (hasNext()) {            
            return keySlice.get(pos++).getKey();
        } else {
            throw new NoSuchElementException();
        }        
    }
}
