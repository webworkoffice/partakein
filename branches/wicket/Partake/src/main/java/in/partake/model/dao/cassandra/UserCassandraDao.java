package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.User;
import in.partake.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.Cassandra.Client;

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
    public void put(PartakeConnection con, User user) throws DAOException {
        if (user == null) { throw new NullPointerException(); }
        if (user.getId() == null) { throw new NullPointerException(); }
        
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            putImpl(ccon.getClient(), user, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public User find(PartakeConnection con, String id) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return findImpl(ccon.getClient(), id);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void remove(PartakeConnection con, String userId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {            
            removeImpl(ccon.getClient(), userId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public DataIterator<User> getIterator(PartakeConnection con) throws DAOException {
        return getIteratorImpl((CassandraConnection) con,
                new CassandraTableDescription(USERS_PREFIX, USERS_KEYSPACE, USERS_COLUMNFAMILY, USERS_CL_R, USERS_CL_W),
                this);
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        removeAllData((CassandraConnection) con);
    }
    
    // ----------------------------------------------------------------------
    // insertion
    
    private void putImpl(Client client, User user, long time) throws Exception {
        String key = USERS_PREFIX + user.getId();

        List<Mutation> mutations = new ArrayList<Mutation>();
        mutations.add(createMutation("twitterId", user.getTwitterId(), time));
        mutations.add(createMutation("calendarId", user.getCalendarId(), time));
        mutations.add(createMutation("lastLoginAt", user.getLastLoginAt(), time));
        mutations.add(createMutation("deleted", "false", time));
        
        client.batch_mutate(USERS_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(USERS_COLUMNFAMILY, mutations)), USERS_CL_W);
    }

    private void removeImpl(Client client, String userId, long time) throws Exception {
        String key = USERS_PREFIX + userId;

        List<Mutation> mutations = new ArrayList<Mutation>();
        mutations.add(createMutation("deleted", "true", time));
        
        client.batch_mutate(USERS_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(USERS_COLUMNFAMILY, mutations)), USERS_CL_W);
    }

    private User findImpl(Client client, String userId) throws Exception {
    	String key = USERS_PREFIX + userId;
    	
        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 100);
        predicate.setSlice_range(sliceRange);

        ColumnParent parent = new ColumnParent(USERS_COLUMNFAMILY);

        List<ColumnOrSuperColumn> results = client.get_slice(USERS_KEYSPACE, key, parent, predicate, USERS_CL_R);

        if (results.isEmpty()) { return null; }
        
        User user = new User();
        user.setId(userId);
        
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            String name = string(column.getName());
            String value = string(column.getValue());
            
            if ("twitterId".equals(name)) {
                user.setTwitterId(value);
            } else if ("lastLoginAt".equals(name)) {
            	user.setLastLoginAt(Util.dateFromTimeString(string(column.getValue())));
            } else if ("calendarId".equals(name)) {
                user.setCalendarId(value);
            } else if ("deleted".equals(name)) {
                if ("true".equals(value)) { return null; }
            }
        }
        
        return user.freeze();
    }

}





