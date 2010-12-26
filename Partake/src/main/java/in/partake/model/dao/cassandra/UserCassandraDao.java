package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.User;
import in.partake.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.Cassandra.Client;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

// * USER MASTER TABLE
//      users:id:<user id>
//          twitterId/<twitter id>
//          lastLoginAt/<Date>
//          calendarId/<calendar id>
//
// * USER OPENID TABLE
//      users:openid:<user id>
//          <open id identifier>/null

// TODO: temporarily public. should be removed later.
class UserCassandraDao extends CassandraDao implements IUserAccess {
    
	// USER MASTER TABLE
    private static final String USERS_PREFIX = "users:id:";
    private static final String USERS_KEYSPACE = "Keyspace1";
    private static final String USERS_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel USERS_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel USERS_CL_W = ConsistencyLevel.ALL;
    
    // USER OPENID TABLE
    private static final String USERS_OPENID_PREFIX = "users:openid:";
    private static final String USERS_OPENID_KEYSPACE = "Keyspace1";
    private static final String USERS_OPENID_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel USERS_OPENID_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel USERS_OPENID_CL_W = ConsistencyLevel.ALL;
    
    // ----------------------------------------------------------------------
    
    UserCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    // fresh な user id を１つ作成して返す。
    @Override
    public String getFreshUserId(PartakeConnection con) throws DAOException {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public void addUser(PartakeConnection con, String userId, int twitterId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addUserWithId(ccon.getClient(), userId, twitterId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public User getUserById(PartakeConnection con, String id) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getUserById(ccon.getClient(), id);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void updateLastLogin(PartakeConnection con, User user) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            updateUserField(ccon.getClient(), user.getId(), "lastLoginAt", Util.getTimeString(ccon.getAcquiredTime()), ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void updateCalendarId(PartakeConnection con, User user, String calendarId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            updateUserField(ccon.getClient(), user.getId(), "calendarId", calendarId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public List<User> getUsersByIds(PartakeConnection con, List<String> ids) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            ArrayList<User> users = new ArrayList<User>();
            for (String id : ids) {
                User user = getUserById(ccon.getClient(), id);
                users.add(user);
            }
            
            return users;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    // ----------------------------------------------------------------------
    // Open ID

    /* (non-Javadoc)
     * @see in.partake.dao.cassandra.IUserAccess#addOpenID(java.lang.String, java.lang.String)
     */
    @Override
    public void addOpenID(PartakeConnection con, String userId, String identity) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addOpenID(ccon.getClient(), userId, identity, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    /* (non-Javadoc)
     * @see in.partake.dao.cassandra.IUserAccess#getOpenIDIdentifiers(java.lang.String)
     */
    // TODO: DataIterator じゃなくて List<String> 返すべきじゃないかなあ...。
    @Override
    public DataIterator<String> getOpenIDIdentifiers(PartakeConnection connection, String userId) throws DAOException {
        try { 
            return getOpenIDIdentitiesImpl((CassandraConnection) connection, userId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void removeOpenID(PartakeConnection con, String userId, String identifier) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            removeOpenID(ccon.getClient(), userId, identifier, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    // ----------------------------------------------------------------------
    // insertion
    
    private String addUserWithId(Client client, String id, int twitterId, long time) throws Exception {
    	String key = USERS_PREFIX + id;

        Map<String, List<ColumnOrSuperColumn>> cfmap = new HashMap<String, List<ColumnOrSuperColumn>>();
        List<ColumnOrSuperColumn> columns = new ArrayList<ColumnOrSuperColumn>();

        columns.add(new ColumnOrSuperColumn().setColumn(new Column(bytes("id"), bytes(id), time)));
        columns.add(new ColumnOrSuperColumn().setColumn(new Column(bytes("twitterId"), bytes(String.valueOf(twitterId)), time)));
        columns.add(new ColumnOrSuperColumn().setColumn(new Column(bytes("lastLoginAt"), bytes(Util.getTimeString(time)), time)));
        
        cfmap.put(USERS_COLUMNFAMILY, columns);
        
        client.batch_insert(USERS_KEYSPACE, key, cfmap, USERS_CL_W);
        
        return id;
    }
    
    private void updateUserField(Client client, String userId, String name, String value, long time) throws Exception {
    	String key = USERS_PREFIX + userId;
    	
    	ColumnPath columnPath = new ColumnPath(USERS_COLUMNFAMILY);
    	columnPath.setColumn(bytes(name));
    	
    	client.insert(USERS_KEYSPACE, key, columnPath, bytes(value), time, USERS_CL_W);    	
    }

    // ----------------------------------------------------------------------
    // retrieval

    private User getUserById(Client client, String id) throws Exception {
    	String key = USERS_PREFIX + id;
    	
        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 100);
        predicate.setSlice_range(sliceRange);

        ColumnParent parent = new ColumnParent(USERS_COLUMNFAMILY);

        List<ColumnOrSuperColumn> results = client.get_slice(USERS_KEYSPACE, key, parent, predicate, USERS_CL_R);

        if (results.isEmpty()) { return null; }
        
        User user = new User();
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            String name = string(column.getName());
            String value = string(column.getValue());
            
            if ("id".equals(name)) {
                user.setId(value);
            } else if ("twitterId".equals(name)) {
                user.setTwitterId(Integer.parseInt(value));
            } else if ("lastLoginAt".equals(name)) {
            	user.setLastLoginAt(Util.dateFromTimeString(string(column.getValue())));
            } else if ("calendarId".equals(name)) {
                user.setCalendarId(value);
            }
        }
        
        return user;
    }
    
    // ----------------------------------------------------------------------
    // Open ID
    
    private void addOpenID(Client client, String userId, String identity, long time) throws Exception {
        String key = USERS_OPENID_PREFIX + userId;

        List<Mutation> mutations = new ArrayList<Mutation>(); 
        mutations.add(createColumnMutation(identity, EMPTY, time));
        
        client.batch_mutate(USERS_OPENID_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(USERS_OPENID_COLUMNFAMILY, mutations)), USERS_OPENID_CL_W);
    }
    
    private void removeOpenID(Client client, String userId, String identifier, long time) throws Exception {
        String key = USERS_OPENID_PREFIX + userId;

        List<Mutation> mutations = new ArrayList<Mutation>(); 
        mutations.add(createDeleteMutation(identifier, time));
        
        client.batch_mutate(USERS_OPENID_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(USERS_OPENID_COLUMNFAMILY, mutations)), USERS_OPENID_CL_W);
    }
    
    private CassandraDataIterator<String> getOpenIDIdentitiesImpl(CassandraConnection con, String userId) throws Exception {
        String key = USERS_OPENID_PREFIX + userId;
        
        ColumnIterator iterator = new ColumnIterator(con, factory, USERS_OPENID_KEYSPACE, key, USERS_OPENID_COLUMNFAMILY, false, USERS_OPENID_CL_R, USERS_OPENID_CL_W);
        return new CassandraDataIterator<String>(iterator, new ColumnOrSuperColumnMapper<String>(con, factory) {
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





