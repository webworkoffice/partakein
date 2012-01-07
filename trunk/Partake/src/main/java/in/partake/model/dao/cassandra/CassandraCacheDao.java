package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.ICacheAccess;
import in.partake.model.dto.CacheData;
import in.partake.util.Util;

public class CassandraCacheDao extends CassandraDao implements ICacheAccess {
    // MASTER TABLE
    private static final String CACHE_PREFIX = "cache:id:";
    private static final String CACHE_KEYSPACE = "Keyspace1";
    private static final String CACHE_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel CACHE_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel CACHE_CL_W = ConsistencyLevel.ALL;
    
    public CassandraCacheDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    @Override
    public void put(PartakeConnection con, CacheData cacheData) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        if (cacheData.getId() == null) { throw new NullPointerException("id should not be null."); }
        try {
            addCacheImpl(ccon.getClient(), cacheData, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public CacheData find(PartakeConnection con, String cacheId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getCacheImpl(ccon.getClient(), cacheId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void remove(PartakeConnection con, String cacheId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            removeCacheImpl(ccon.getClient(), cacheId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public DataIterator<CacheData> getIterator(PartakeConnection con) throws DAOException {
        return getIteratorImpl((CassandraConnection) con, new CassandraTableDescription(CACHE_PREFIX, CACHE_KEYSPACE, CACHE_COLUMNFAMILY, CACHE_CL_R, CACHE_CL_W), this);
    }

    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        removeAllData((CassandraConnection) con);
    }
    
    // ----------------------------------------------------------------------
    
    private void addCacheImpl(Client client, CacheData data, long time) throws Exception {
        String key = CACHE_PREFIX + data.getId();

        List<Mutation> mutations = new ArrayList<Mutation>(); 

        mutations.add(createColumnMutation("data", data.getData(), time));
        mutations.add(createMutation("invalidAfter", data.getInvalidAfter(), time));
        
        client.batch_mutate(CACHE_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(CACHE_COLUMNFAMILY, mutations)), CACHE_CL_W);
    }
    
    private void removeCacheImpl(Client client, String cacheId, long time) throws Exception {
        String key = CACHE_PREFIX + cacheId;

        ColumnPath columnPath = new ColumnPath(CACHE_COLUMNFAMILY);
        client.remove(CACHE_KEYSPACE, key, columnPath, time, CACHE_CL_W);        
    }
    
    private CacheData getCacheImpl(Client client, String id, long time) throws Exception {
        String key = CACHE_PREFIX + id;

        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 100);
        predicate.setSlice_range(sliceRange);

        ColumnParent parent = new ColumnParent(CACHE_COLUMNFAMILY);
        List<ColumnOrSuperColumn> results = client.get_slice(CACHE_KEYSPACE, key, parent, predicate, CACHE_CL_R);

        if (results == null || results.isEmpty()) { return null; }
        
        CacheData data = new CacheData();
        data.setId(id);
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            String name = string(column.getName());
            
            if ("data".equals(name)) {
                data.setData(column.getValue());
            } else if ("invalidAfter".equals(name)) {
                data.setInvalidAfter(Util.dateFromTimeString(string(column.getValue())));
            }
        }
        
        // the data is already invalid. remove it.
        if (data.getInvalidAfter() != null && data.getInvalidAfter().before(new Date())) {
            removeCacheImpl(client, id, time);
            return null;
        }
        
        return data.freeze();
    }
}
