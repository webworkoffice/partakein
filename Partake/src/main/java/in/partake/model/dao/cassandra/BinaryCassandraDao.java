package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.string;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IBinaryAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.BinaryData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import me.prettyprint.cassandra.service.CassandraClient;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;

class BinaryCassandraDao extends CassandraDao implements IBinaryAccess {
    // BINARY MASTER TABLE
    private static final String BINARY_PREFIX = "binaries:id:";
    private static final String BINARY_KEYSPACE = "Keyspace1";
    private static final String BINARY_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel BINARY_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel BINARY_CL_W = ConsistencyLevel.ALL;

    BinaryCassandraDao() {
        // do nothing for now.
    }
    
    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return UUID.randomUUID().toString();
    }

    @Override
    public void addBinaryWithId(PartakeConnection con, String id, BinaryData embryo) throws DAOException {
        PartakeCassandraConnection ccon = (PartakeCassandraConnection) con;
        try {
            addBinaryWithId(ccon.getClient(), id, embryo, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    private void addBinaryWithId(Client client, String id, BinaryData embryo, long time) throws Exception {
        String key = BINARY_PREFIX + id;

        List<Mutation> mutations = new ArrayList<Mutation>(); 

        mutations.add(createColumnMutation("type", embryo.getType(), time));
        mutations.add(createColumnMutation("content", embryo.getData(), time));
        
        client.batch_mutate(BINARY_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(BINARY_COLUMNFAMILY, mutations)), BINARY_CL_W);
    }
    
    
    @Override
    public BinaryData getBinaryById(PartakeConnection con, String id) throws DAOException {
        PartakeCassandraConnection ccon = (PartakeCassandraConnection) con;
        try {
            return getBinaryById(ccon.getClient(), id);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    private BinaryData getBinaryById(Client client, String id) throws Exception {
        String key = BINARY_PREFIX + id;

        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 100);
        predicate.setSlice_range(sliceRange);

        ColumnParent parent = new ColumnParent(BINARY_COLUMNFAMILY);
        List<ColumnOrSuperColumn> results = client.get_slice(BINARY_KEYSPACE, key, parent, predicate, BINARY_CL_R);

        if (results == null || results.isEmpty()) { return null; }
        
        BinaryData data = new BinaryData();
        data.setId(id);
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            String name = string(column.getName());
            
            if ("content".equals(name)) {
                data.setDate(column.getValue());
            } else if ("type".equals(name)) {
                data.setType(string(column.getValue()));
            }
        }
        
        return data.freeze();
    }
    

    @Override
    public void removeBinary(PartakeConnection con, String id) throws DAOException {
        PartakeCassandraConnection ccon = (PartakeCassandraConnection) con;
        try {
            removeBinary(ccon.getClient(), id, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    private void removeBinary(Client client, String id, long time) throws Exception {
        String key = BINARY_PREFIX + id;
        
        ColumnPath columnPath = new ColumnPath(BINARY_COLUMNFAMILY);
        client.remove(BINARY_KEYSPACE, key, columnPath, time, BINARY_CL_W);
    }
}


