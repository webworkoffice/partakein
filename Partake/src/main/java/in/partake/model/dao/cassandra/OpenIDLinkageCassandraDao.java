package in.partake.model.dao.cassandra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IOpenIDLinkageAccess;
import in.partake.model.dao.PartakeConnection;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.log4j.Logger;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

// * OPEN ID LINKAGE MASTER
//     openid:id:<user identifier>
//         userId/<user id>


class OpenIDLinkageCassandraDao extends CassandraDao implements IOpenIDLinkageAccess {
    private static final Logger logger = Logger.getLogger(OpenIDLinkageCassandraDao.class);
    
    // OPEN ID MASTER TABLE
    private static final String OPENID_LINKAGE_PREFIX = "openid:id:";
    private static final String OPENID_LINKAGE_KEYSPACE = "Keyspace1";
    private static final String OPENID_LINKAGE_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel OPENID_LINKAGE_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel OPENID_LINKAGE_CL_W = ConsistencyLevel.ALL;
    
    // USER OPENID TABLE
    private static final String USERS_OPENID_PREFIX = "users:openid:";
    private static final String USERS_OPENID_KEYSPACE = "Keyspace1";
    private static final String USERS_OPENID_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel USERS_OPENID_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel USERS_OPENID_CL_W = ConsistencyLevel.ALL;

    public OpenIDLinkageCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    /* (non-Javadoc)
     * @see in.partake.dao.cassandra.IOpenIDLinkageAccess#getUserId(java.lang.String)
     */
    @Override
    public String getUserId(PartakeConnection con, String identifier) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getUserId(ccon.getClient(), identifier);
        } catch (Exception e) {
            throw new DAOException(e);
        }  
    }
    
    /* (non-Javadoc)
     * @see in.partake.dao.cassandra.IOpenIDLinkageAccess#addOpenID(java.lang.String, java.lang.String)
     */
    @Override
    public void addOpenID(PartakeConnection con, String identifier, String userId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addOpenIDToUserTable(ccon.getClient(), userId, identifier, ccon.getAcquiredTime());
            addOpenIDToMasterTable(ccon.getClient(), identifier, userId, ccon.getAcquiredTime());
            
        } catch (Exception e) {
            throw new DAOException(e);
        }        
    }
    
    @Override
    public void removeOpenID(PartakeConnection con, String identifier) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            String userId = getUserId(con, identifier);
            removeOpenIdFromMasterTable(ccon.getClient(), identifier, con.getAcquiredTime());
            removeOpenIDFromUserTable(ccon.getClient(), userId, identifier, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }  
    }
    
    /* (non-Javadoc)
     * @see in.partake.dao.cassandra.IUserAccess#getOpenIDIdentifiers(java.lang.String)
     */
    @Override
    public DataIterator<String> getOpenIDIdentifiers(PartakeConnection connection, String userId) throws DAOException {
        try { 
            return getOpenIDIdentitiesImpl((CassandraConnection) connection, userId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        removeAllData((CassandraConnection) con);        
    }
    
    // ----------------------------------------------------------------------
    
    private void addOpenIDToMasterTable(Client client, String identity, String userId, long time) throws Exception {
        String key = OPENID_LINKAGE_PREFIX + identity;

        List<Mutation> mutations = new ArrayList<Mutation>(); 
        mutations.add(createMutation("userId", userId, time));
        
        client.batch_mutate(OPENID_LINKAGE_KEYSPACE, 
                        Collections.singletonMap(key, Collections.singletonMap(OPENID_LINKAGE_COLUMNFAMILY, mutations)), 
                        OPENID_LINKAGE_CL_W);
        
        logger.info("addOpenID:" + key);
    }
    
    private void addOpenIDToUserTable(Client client, String userId, String identity, long time) throws Exception {
        String key = USERS_OPENID_PREFIX + userId;

        List<Mutation> mutations = new ArrayList<Mutation>(); 
        mutations.add(createColumnMutation(identity, EMPTY, time));
        
        client.batch_mutate(USERS_OPENID_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(USERS_OPENID_COLUMNFAMILY, mutations)), USERS_OPENID_CL_W);
    }
    
    private void removeOpenIdFromMasterTable(Client client, String identifier, long time) throws Exception {
        String key = OPENID_LINKAGE_PREFIX + identifier;
        
        List<Mutation> mutations = new ArrayList<Mutation>();
        mutations.add(createDeleteMutation("userId", time));

        client.batch_mutate(OPENID_LINKAGE_KEYSPACE, 
                        Collections.singletonMap(key, Collections.singletonMap(OPENID_LINKAGE_COLUMNFAMILY, mutations)), 
                        OPENID_LINKAGE_CL_W);
    }
    
    private void removeOpenIDFromUserTable(Client client, String userId, String identifier, long time) throws Exception {
        String key = USERS_OPENID_PREFIX + userId;

        List<Mutation> mutations = new ArrayList<Mutation>(); 
        mutations.add(createDeleteMutation(identifier, time));
        
        client.batch_mutate(USERS_OPENID_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(USERS_OPENID_COLUMNFAMILY, mutations)), USERS_OPENID_CL_W);
    }
    
    private String getUserId(Client client, String identity) throws Exception {
        String key = OPENID_LINKAGE_PREFIX + identity;
        
        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 100);
        predicate.setSlice_range(sliceRange);

        ColumnParent parent = new ColumnParent(OPENID_LINKAGE_COLUMNFAMILY);
        
        List<ColumnOrSuperColumn> results =
            client.get_slice(OPENID_LINKAGE_KEYSPACE, key, parent, predicate, OPENID_LINKAGE_CL_R);
        
        for (ColumnOrSuperColumn cosc : results) {
            if (cosc == null) { continue; }
            String name = string(cosc.column.name);
            if ("userId".equals(name)) { return string(cosc.column.value); }
        }
        
        return null;
    }
    
    private CassandraColumnDataIterator<String> getOpenIDIdentitiesImpl(CassandraConnection con, String userId) throws Exception {
        String key = USERS_OPENID_PREFIX + userId;
        
        ColumnIterator iterator = new ColumnIterator(con, factory, USERS_OPENID_KEYSPACE, key, USERS_OPENID_COLUMNFAMILY, false, USERS_OPENID_CL_R, USERS_OPENID_CL_W);
        return new CassandraColumnDataIterator<String>(iterator, new ColumnOrSuperColumnMapper<String>(con, factory) {
            @Override
            public String map(ColumnOrSuperColumn cosc) throws DAOException {
                Column column = cosc.getColumn();
                return string(column.getName());
            }
            
            @Override
            public ColumnOrSuperColumn unmap(String t) throws DAOException {
                long time = new Date().getTime();
                ColumnOrSuperColumn cosc = new ColumnOrSuperColumn();
                cosc.setColumn(new Column(bytes(t), EMPTY, time));
                
                return cosc;
            }
        });
    }
}
