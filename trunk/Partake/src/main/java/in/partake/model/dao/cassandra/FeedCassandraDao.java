package in.partake.model.dao.cassandra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IFeedAccess;
import in.partake.model.dao.PartakeConnection;

// import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

/**
 * Since some events may be private, the feed id should not be able to be guessed from the event id.
 * So the feed id should be different from the event id. 
 */
class FeedCassandraDao extends CassandraDao implements IFeedAccess {
    // FEED MASTER TABLE
    // MASTER TABLE は後で使う予定。
//    private static final String FEED_PREFIX = "feeds:id:";
//    private static final String FEED_KEYSPACE = "Keyspace1";
//    private static final String FEED_COLUMNFAMILY = "Standard2";
//    private static final ConsistencyLevel FEED_CL_R = ConsistencyLevel.ONE;
//    private static final ConsistencyLevel FEED_CL_W = ConsistencyLevel.ALL;

    // TODO: いやー、これは完全に RDB 脳ですね。まあ、いいや。
    // PREFIX + <EVENT ID> -> FEED ID
    private static final String FEED_EVENT_PREFIX = "feeds:event:";
    private static final String FEED_EVENT_KEYSPACE = "Keyspace1";
    private static final String FEED_EVENT_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel FEED_EVENT_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel FEED_EVENT_CL_W = ConsistencyLevel.ALL;

    // PREFIX + <FEED ID> -> EVENT ID
    private static final String FEED_RELATION_PREFIX = "feeds:rel:";
    private static final String FEED_RELATION_KEYSPACE = "Keyspace1";
    private static final String FEED_RELATION_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel FEED_RELATION_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel FEED_RELATION_CL_W = ConsistencyLevel.ALL;

    FeedCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public void addFeedId(PartakeConnection con, String feedId, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addToEvent(ccon.getClient(), eventId, feedId, ccon.getAcquiredTime());
            addToFeed(ccon.getClient(), feedId, eventId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }        
    }
    
    @Override
    public String getEventIdByFeedId(PartakeConnection con, String feedId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getEventIdByFeedId(ccon.getClient(), feedId);
        } catch (Exception e) {
            throw new DAOException(e);
        }        
    }
    
    @Override
    public String getFeedIdByEventId(PartakeConnection con, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getFeedIdByEventId(ccon.getClient(), eventId);
        } catch (Exception e) {
            throw new DAOException(e);
        } 
    }
    
    // ----------------------------------------------------------------------
    public void addToEvent(Client client, String eventId, String feedId, long time) throws Exception {
        String key = FEED_EVENT_PREFIX + eventId;

        List<Mutation> mutations = new ArrayList<Mutation>(); 
        mutations.add(createMutation("feedId", feedId, time));
        
        client.batch_mutate(FEED_EVENT_KEYSPACE, 
                        Collections.singletonMap(key, Collections.singletonMap(FEED_EVENT_COLUMNFAMILY, mutations)), FEED_EVENT_CL_W);
    }
    
    private String getFeedIdByEventId(Client client, String eventId) throws Exception {
        String key = FEED_EVENT_PREFIX + eventId;

        ColumnOrSuperColumn cosc = get(client, FEED_EVENT_KEYSPACE, FEED_EVENT_COLUMNFAMILY, "feedId", key, FEED_EVENT_CL_R);
        if (cosc != null) {
            return string(cosc.getColumn().getValue());
        } else {
            return null;
        }
    }

    public void addToFeed(Client client, String feedId, String eventId, long time) throws Exception {
        String key = FEED_RELATION_PREFIX + feedId;

        List<Mutation> mutations = new ArrayList<Mutation>(); 
        mutations.add(createMutation("eventId", eventId, time));
        
        client.batch_mutate(FEED_RELATION_KEYSPACE, 
                        Collections.singletonMap(key, Collections.singletonMap(FEED_RELATION_COLUMNFAMILY, mutations)), FEED_RELATION_CL_W);
    }
    
    private String getEventIdByFeedId(Client client, String eventId) throws Exception {
        String key = FEED_RELATION_PREFIX + eventId;

        ColumnOrSuperColumn cosc = get(client, FEED_RELATION_KEYSPACE, FEED_RELATION_COLUMNFAMILY, "eventId", key, FEED_RELATION_CL_R);
        if (cosc == null) {
            return null;
        } else {
            return string(cosc.getColumn().getValue());
        }
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        removeAllData((CassandraConnection) con);
    }
}

