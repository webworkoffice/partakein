package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.User;
import in.partake.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
    
    // ----------------------------------------------------------------------
    
    UserCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    // fresh な user id を１つ作成して返す。
    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public void createUser(PartakeConnection con, User user) throws DAOException {
        if (user == null) { throw new NullPointerException(); }
        if (user.getId() == null) { throw new NullPointerException(); }
        
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addUserImpl(ccon.getClient(), user, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void updateUser(PartakeConnection con, User user) throws DAOException {
        createUser(con, user);
    }
    
    @Override
    public User getUser(PartakeConnection con, String id) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getUserById(ccon.getClient(), id);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void updateLastLogin(PartakeConnection con, String userId, Date now) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            updateUserField(ccon.getClient(), userId, "lastLoginAt", Util.getTimeString(now.getTime()), ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        removeAllData((CassandraConnection) con);
    }
    
    // ----------------------------------------------------------------------
    // insertion
    
    private void addUserImpl(Client client, User user, long time) throws Exception {
        String key = USERS_PREFIX + user.getId();

        List<Mutation> mutations = new ArrayList<Mutation>();
        mutations.add(createMutation("twitterId", String.valueOf(user.getTwitterId()), time));
        mutations.add(createMutation("calendarId", user.getCalendarId(), time));
        mutations.add(createMutation("lastLoginAt", user.getLastLoginAt(), time));
        
        client.batch_mutate(USERS_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(USERS_COLUMNFAMILY, mutations)), USERS_CL_W);
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
        user.setId(id);
        
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            String name = string(column.getName());
            String value = string(column.getValue());
            
            if ("twitterId".equals(name)) {
                user.setTwitterId(Integer.parseInt(value));
            } else if ("lastLoginAt".equals(name)) {
            	user.setLastLoginAt(Util.dateFromTimeString(string(column.getValue())));
            } else if ("calendarId".equals(name)) {
                user.setCalendarId(value);
            }
        }
        
        return user.freeze();
    }

}





