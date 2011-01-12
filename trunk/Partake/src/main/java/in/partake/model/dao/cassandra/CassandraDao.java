package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;

import in.partake.util.Util;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Deletion;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.SuperColumn;

/**
 * The base class of data access object for Cassandra.
 * @author shinyak
 *
 */
abstract class CassandraDao {
    // utility constants.
    public static final byte[] TRUE = bytes("true"); //$NON-NLS-1$
    public static final byte[] FALSE = bytes("false"); //$NON-NLS-1$
    public static final byte[] EMPTY = bytes("");
    
    protected final CassandraDAOFactory factory;
    
    protected CassandraDao(CassandraDAOFactory factory) {
        this.factory = factory;
    }
    
    // ----------------------------------------------------------------------
    // Utility methods
    
    protected ColumnOrSuperColumn get(Client client, String keySpace, String columnFamily, String columnName, String key, ConsistencyLevel readConsistency) throws Exception {
        try {
            ColumnPath columnPath = new ColumnPath();
            columnPath.setColumn_family(columnFamily);
            columnPath.setColumn(bytes(columnName));
            return client.get(keySpace, key, columnPath, readConsistency);        
        } catch (NotFoundException e) {
            // e is intentionally ignored.
            return null;
        }
    }
    
    /**
     * get a slice 
     */
    protected List<ColumnOrSuperColumn> getSlice(Client client, String keySpace, String columnFamily, String key, ConsistencyLevel readConsistency) throws Exception {
        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 1000);
        predicate.setSlice_range(sliceRange);
    
        ColumnParent parent = new ColumnParent(columnFamily);
        List<ColumnOrSuperColumn> results = client.get_slice(keySpace, key, parent, predicate, readConsistency);
        
        return results;
    }
    
    protected Mutation createMutation(String key, String value, long time) {
        if (value != null) {
            return createColumnMutation(key, bytes(value), time);
        } else {
            return createDeleteMutation(key, time);
        }
    }
    
    protected Mutation createMutation(String key, Date date, long time) {
        if (date != null) {
            return createColumnMutation(key, bytes(Util.getTimeString(date)), time);
        } else {
            return createDeleteMutation(key, time);
        }
    }
    
    @Deprecated
    protected Mutation createColumnMutation(String key, String value, long time) {
        return createColumnMutation(key, bytes(value), time);
    }
    
    protected Mutation createColumnMutation(String key, byte[] value, long time) {
        Mutation mutation = new Mutation();
        mutation.setColumn_or_supercolumn(new ColumnOrSuperColumn().setColumn(new Column(bytes(key), value, time)));
        return mutation;
    }
    
    protected Mutation createSuperColumnMutation(SuperColumn superColumn) {
        Mutation mutation = new Mutation();
        mutation.setColumn_or_supercolumn(new ColumnOrSuperColumn().setSuper_column(superColumn));
        return mutation;
    }
    
    protected Mutation createDeleteMutation(String key, long time) {
        Mutation mutation = new Mutation();
        Deletion deletion = new Deletion(time);
        deletion.predicate = new SlicePredicate().setColumn_names(Collections.singletonList(bytes(key)));
        mutation.setDeletion(deletion);
        return mutation;
    }
}
