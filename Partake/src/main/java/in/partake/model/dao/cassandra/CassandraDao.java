package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;

import java.util.Date;
import java.util.List;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
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
        return CassandraDaoUtils.get(client, keySpace, columnFamily, columnName, key, readConsistency);
    }
    
    protected SuperColumn getSuperColumn(Client client, String keySpace, String columnFamily, String columnName, String key, ConsistencyLevel readConsistency) throws Exception {
        return CassandraDaoUtils.getSuperColumn(client, keySpace, columnFamily, columnName, key, readConsistency);
    }
    
    /**
     * get a slice 
     */
    protected List<ColumnOrSuperColumn> getSlice(Client client, String keySpace, String columnFamily, String key, ConsistencyLevel readConsistency) throws Exception {
        return CassandraDaoUtils.getSlice(client, keySpace, columnFamily, key, readConsistency);
    }
    
    protected Mutation createMutation(String key, String value, long time) {
        return CassandraDaoUtils.createMutation(key, value, time);
    }
    
    protected Mutation createMutation(String key, Date date, long time) {
        return CassandraDaoUtils.createMutation(key, date, time);
    }
    
    @Deprecated
    protected Mutation createColumnMutation(String key, String value, long time) {
        return CassandraDaoUtils.createColumnMutation(key, value, time);
    }
    
    protected Mutation createColumnMutation(String key, byte[] value, long time) {
        return CassandraDaoUtils.createColumnMutation(key, value, time);
    }
    
    protected Mutation createSuperColumnMutation(SuperColumn superColumn) {
        return CassandraDaoUtils.createSuperColumnMutation(superColumn);
    }
    
    protected Mutation createDeleteMutation(String key, long time) {
        return CassandraDaoUtils.createDeleteMutation(key, time);
    }
}
