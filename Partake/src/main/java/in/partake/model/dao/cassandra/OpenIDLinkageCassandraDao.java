package in.partake.model.dao.cassandra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IOpenIDLinkageAccess;
import in.partake.model.dao.PartakeConnection;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.log4j.Logger;

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

    public OpenIDLinkageCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    /* (non-Javadoc)
     * @see in.partake.dao.cassandra.IOpenIDLinkageAccess#addOpenID(java.lang.String, java.lang.String)
     */
    @Override
    public void addOpenID(PartakeConnection con, String identifier, String userId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addOpenID(ccon.getClient(), identifier, userId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }        
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
    
    @Override
    public void removeOpenID(PartakeConnection con, String identifier, String userId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            removeOpenId(ccon.getClient(), identifier, userId, con.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }  
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        
    }
    
    // ----------------------------------------------------------------------
    
    private void addOpenID(Client client, String identity, String userId, long time) throws Exception {
        String key = OPENID_LINKAGE_PREFIX + identity;

        List<Mutation> mutations = new ArrayList<Mutation>(); 
        mutations.add(createMutation("userId", userId, time));
        
        client.batch_mutate(OPENID_LINKAGE_KEYSPACE, 
                        Collections.singletonMap(key, Collections.singletonMap(OPENID_LINKAGE_COLUMNFAMILY, mutations)), 
                        OPENID_LINKAGE_CL_W);
        
        logger.info("addOpenID:" + key);
    }
    
    private void removeOpenId(Client client, String identifier, String userId, long time) throws Exception {
        String key = OPENID_LINKAGE_PREFIX + identifier;
        
        List<Mutation> mutations = new ArrayList<Mutation>();
        mutations.add(createDeleteMutation("userId", time));

        client.batch_mutate(OPENID_LINKAGE_KEYSPACE, 
                        Collections.singletonMap(key, Collections.singletonMap(OPENID_LINKAGE_COLUMNFAMILY, mutations)), 
                        OPENID_LINKAGE_CL_W);
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
}
