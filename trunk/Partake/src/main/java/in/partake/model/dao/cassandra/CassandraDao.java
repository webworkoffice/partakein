package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IAccess;
import in.partake.model.dto.PartakeModel;
import in.partake.util.Util;

import java.util.Date;
import java.util.List;

import org.apache.cassandra.thrift.Column;
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
        return CassandraDaoUtils.getColumn(client, keySpace, columnFamily, columnName, key, readConsistency);
    }
    
    protected ColumnOrSuperColumn getSuperColumn(Client client, String keySpace, String columnFamily, String columnName, String key, ConsistencyLevel readConsistency) throws Exception {
        return CassandraDaoUtils.getSuperColumn(client, keySpace, columnFamily, columnName, key, readConsistency);
    }
    
    /**
     * get a slice 
     */
    protected List<ColumnOrSuperColumn> getSlice(Client client, String keySpace, String columnFamily, String key, ConsistencyLevel readConsistency) throws Exception {
        return CassandraDaoUtils.getSlice(client, keySpace, columnFamily, key, readConsistency);
    }

    protected Column createColumn(String name, String value, long time) {
        return new Column(bytes(name), bytes(value), time);
    }
    
    protected Column createColumn(String name, Date value, long time) {
        return createColumn(name, Util.getTimeString(value), time);
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
    
    // ----------------------------------------------------------------------
    //
    
    /**
     * table description に基づいて、key で iterate を行う。
     * Primary Key は String に限定。
     * Primary Key が String でないものは、ColumnIterator によって iterate する必要がある。
     */
    protected <T extends PartakeModel<T>> DataIterator<T> getIteratorImpl(CassandraConnection con, CassandraTableDescription desc, IAccess<T, String> access) throws DAOException { 
        CassandraKeyIterator it = new CassandraKeyIterator(con, desc.keyspace, desc.prefix, desc.columnFamily, desc.readConsistency);
        
        class KeyMapperWithAccess<S extends PartakeModel<S>> extends KeyMapper<S> {
            private IAccess<S, String> access;
            public KeyMapperWithAccess(CassandraConnection con, IAccess<S, String> access) {
                super(con);
                this.access = access;
            }
            
            @Override
            protected S map(String key) throws DAOException {
                return access.find(getConnection(), key);
            }
        }
        
        return new CassandraKeyDataIterator<T>(it, new KeyMapperWithAccess<T>(con, access));
    }
    
    protected void removeAllData(CassandraConnection con) throws DAOException {
        
        long now = new Date().getTime();
        try {
            removeAllData(con, "Standard2", now);
            removeAllData(con, "Super1", now);
        } catch (Exception e) {
            throw new DAOException(e);
        }
        
    }
    
    private void removeAllData(CassandraConnection con, String columnFamily, long now) throws Exception {
        // TODO: Since it takes too much time, we do not remove the data for now.
//        KeyIterator it = new CassandraKeyIterator(con, "Keyspace1", "", columnFamily, ConsistencyLevel.ALL);
//        while (it.hasNext()) {
//            String key = it.nextWithPrefix();
//            ColumnPath columnPath = new ColumnPath(columnFamily);
//            con.getClient().remove("Keyspace1", key, columnPath, now, ConsistencyLevel.ALL);
//        }        
    }
}
