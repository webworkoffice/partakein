package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.string;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeModelFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import me.prettyprint.cassandra.service.CassandraClient;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.SuperColumn;

// これは user がいじるべきクラスではないので、public class ではない。
class ColumnIterator {
	private String keyspace;
	private String key;
	private ColumnParent parent;
	private ConsistencyLevel readConsistencyLevel;
	private ConsistencyLevel writeConsistencyLevel;
	
	private byte[] nextStartKey;	// null if all elements are retrieved.
	boolean shouldExcludeNextStartKey; 
	boolean reverseOrder;
	
	// either connection or factory should be null. 
	private PartakeCassandraConnection connection;
	private CassandraDAOFactory factory;
	
	private int pos;
	private List<ColumnOrSuperColumn> cached;
	private ColumnOrSuperColumn current;

    public ColumnIterator(PartakeCassandraConnection connection, String keyspace, String key, String columnFamily, boolean reverseOrder, 
                    ConsistencyLevel readConsistencyLevel, ConsistencyLevel writeConsistencyLevel) {
        this.keyspace = keyspace;
        this.key = key;
        this.parent = new ColumnParent(columnFamily);
        this.readConsistencyLevel = readConsistencyLevel;
        this.writeConsistencyLevel = writeConsistencyLevel;
        
        this.nextStartKey = new byte[0];
        this.shouldExcludeNextStartKey = false;
        this.reverseOrder = reverseOrder;
        
        this.connection = connection;
        this.factory = null;
        
        this.pos = 0;
        this.cached = new ArrayList<ColumnOrSuperColumn>();
        this.current = null;
    }
	
	
	public ColumnIterator(CassandraDAOFactory factory, String keyspace, String key, String columnFamily, boolean reverseOrder, 
						  ConsistencyLevel readConsistencyLevel, ConsistencyLevel writeConsistencyLevel) {
	    this.keyspace = keyspace;
	    this.key = key;
	    this.parent = new ColumnParent(columnFamily);
	    this.readConsistencyLevel = readConsistencyLevel;
	    this.writeConsistencyLevel = writeConsistencyLevel;

	    this.nextStartKey = new byte[0];
	    this.shouldExcludeNextStartKey = false;
	    this.reverseOrder = reverseOrder;

	    this.connection = null;
	    this.factory = factory;

	    this.pos = 0;
	    this.cached = new ArrayList<ColumnOrSuperColumn>();
	    this.current = null;
	}
	
    public CassandraDAOFactory getFactory() {
        return this.factory;
    }
	   
	public boolean hasNext() throws DAOException {
	    if (pos < cached.size()) { return true; }
		if (nextStartKey == null) { return false; }

		PartakeCassandraConnection con = this.connection != null ? this.connection : (PartakeCassandraConnection)factory.getConnection("ColumnIterator#hasNext");
		try {
			Client cassandra = con.getClient();
			
			SliceRange range = new SliceRange(nextStartKey, new byte[0], reverseOrder, 1000);
			
			SlicePredicate predicate = new SlicePredicate();
			predicate.slice_range = range;
			
			cached = cassandra.get_slice(keyspace, key, parent, predicate, readConsistencyLevel);
			pos = 0;
			
			if (!cached.isEmpty()) {
                byte[] firstKey = getName(cached.get(0));                   
                if (Arrays.equals(firstKey, this.nextStartKey)) {
                    pos += 1;
                }			    
			}
			
			if (cached.size() <= pos) {
				nextStartKey = null;
				return false;
			} else {				
			    byte[] lastKey = getName(cached.get(cached.size() - 1));
				nextStartKey = lastKey;
				shouldExcludeNextStartKey = true;
				
				return true;				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
		    if (this.connection == null) { con.invalidate(); }
		}
	}
	
	public ColumnOrSuperColumn next() throws DAOException {
        if (hasNext()) {
            current = cached.get(pos++);
            return current;
        } else {
            throw new NoSuchElementException();
        }
	}
	
	/**
	 * remove the current object from the DB.
	 * @throws DAOException
	 */
	public void remove() throws DAOException {
        PartakeCassandraConnection con = this.connection != null ? this.connection : (PartakeCassandraConnection)factory.getConnection("ColumnIterator#remove()");
		try {
			Client cassandra = con.getClient();
			ColumnPath columnPath = new ColumnPath(parent.column_family);
			if (parent.super_column != null) {
				columnPath.setSuper_column(parent.super_column);
			}
			if (current.column != null) {
				columnPath.setColumn(current.column.name);
			}
			
			long timestamp = new Date().getTime();
			cassandra.remove(keyspace, key, columnPath, timestamp, writeConsistencyLevel);
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
		    if (this.connection == null) { con.invalidate(); }
		}
	}
	
	public void update(ColumnOrSuperColumn cosc) throws DAOException {
		// key は update できない。
		if ((current.super_column != null && !string(current.super_column.name).equals(string(cosc.super_column.name))) ||
			(current.column != null && !string(current.column.name).equals(string(cosc.column.name)))) {			
			throw new DAOException("NAME cannot be updated."); // もうちょっと具体的な IllegalArgument とかそのへん。update は byte[] value に限っても良いかもしれない。
		}
		
		PartakeCassandraConnection con = this.connection != null ? this.connection : (PartakeCassandraConnection)factory.getConnection("ColumnIterator#update");
		try {
			Client cassandra = con.getClient();
			
	        Map<String, List<ColumnOrSuperColumn>> cfmap = new HashMap<String, List<ColumnOrSuperColumn>>();
	        List<ColumnOrSuperColumn> columns = new ArrayList<ColumnOrSuperColumn>();
	        
	        columns.add(cosc);
	        cfmap.put(parent.column_family, columns);
			
			cassandra.batch_insert(keyspace, key, cfmap, writeConsistencyLevel);			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
		    if (this.connection == null) { con.invalidate(); }
		}
	}
	
	// TODO: the argument should be a ColumnOrSuperColumn object instead of <code>value</code>.
	@Deprecated
	public void update(byte[] value) throws DAOException {
	    PartakeCassandraConnection con = this.connection != null ? this.connection : (PartakeCassandraConnection)factory.getConnection("ColumnIterator#update");
		try {
			Client cassandra = con.getClient();
			ColumnPath columnPath = new ColumnPath(parent.column_family);
			if (parent.super_column != null) {
				columnPath.setSuper_column(parent.super_column);
			}
			columnPath.setColumn(current.column.name);
			
			long timestamp = new Date().getTime();
			cassandra.insert(keyspace, key, columnPath, value, timestamp, writeConsistencyLevel);
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
		    if (this.connection == null) { con.invalidate(); }
		}
	}
	
	// SuperColumn を iterate している場合のみ call してよい。それ以外のときの結果は未定義
	@Deprecated
	public void updateSuperColumn(SuperColumn superColumn) throws DAOException {
	    PartakeCassandraConnection con = this.connection != null ? this.connection : (PartakeCassandraConnection)factory.getConnection("ColumnIterator#updateSuperColumn");       
		try {
			Client cassandra = con.getClient();
			
	        Map<String, List<ColumnOrSuperColumn>> cfmap = new HashMap<String, List<ColumnOrSuperColumn>>();
	        List<ColumnOrSuperColumn> columns = new ArrayList<ColumnOrSuperColumn>();
	        
	        columns.add(new ColumnOrSuperColumn().setSuper_column(superColumn));
	        cfmap.put(parent.column_family, columns);
			
			cassandra.batch_insert(keyspace, key, cfmap, writeConsistencyLevel);			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
		    if (this.connection == null) { con.invalidate(); }
		}
	}
		
	// ----------------------------------------------------------------------
	
	private byte[] getName(ColumnOrSuperColumn cosc) {
        if (cosc.column != null) {
            return cosc.column.name;
        } else {
            return cosc.super_column.name;
        }        
	}
}
