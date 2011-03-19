package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;

// * from id
//      events:id:<event id>
//          <event information>
//
// * from owner id (events whose owner is the user)
//      events:owner:<user id>
//			<event id>/""
//			NOTE: value に特に意味はないので new byte[0] を入れておく
//
// * event リストで、begin date で並べたものとかなんとか。
//		archive されていない event で、現在時刻よりも前の event を列挙したい
//		archive されているイベントは触ることが出来ない。
//

class EventCassandraDao extends CassandraDao implements IEventAccess {
    // private static final Logger logger = Logger.getLogger(EventCassandraDao.class);

    
    // EVENT MASTER TABLE
    private static final String EVENTS_PREFIX = "events:id:";
    private static final String EVENTS_KEYSPACE = "Keyspace1";
    private static final String EVENTS_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel EVENTS_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel EVENTS_CL_W = ConsistencyLevel.ALL;
    
    // Events By Owner
    private static final String EVENTS_BYOWNER_PREFIX = "events:owner:";
    private static final String EVENTS_BYOWNER_KEYSPACE = "Keyspace1";
    private static final String EVENTS_BYOWNER_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel EVENTS_BYOWNER_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel EVENTS_BYOWNER_CL_W = ConsistencyLevel.ALL;
    
    public EventCassandraDao(CassandraDAOFactory factory) {
    	super(factory);
    }

    // -----------------------------------------------------------------------------

    public String getFreshId(PartakeConnection con) throws DAOException {        
        return UUID.randomUUID().toString();
    };
    
    @Override
    public Event find(PartakeConnection con, String id) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getEventImpl(ccon, id);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public DataIterator<Event> getIterator(PartakeConnection connection) throws DAOException {
        CassandraKeyIterator it = new CassandraKeyIterator((CassandraConnection) connection, EVENTS_KEYSPACE, EVENTS_PREFIX, EVENTS_COLUMNFAMILY, EVENTS_CL_R); 
        return new CassandraKeyDataIterator<Event>(it, new KeyMapper<Event>((CassandraConnection) connection) {
            @Override
            public Event map(String key) throws DAOException {
                return factory.getEventAccess().find(getConnection(), key);
            }
        });
    }
    
    @Override
    public List<Event> findByOwnerId(PartakeConnection con, String userId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getEventsByOwnerImpl(ccon, userId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void put(PartakeConnection con, Event embryo) throws DAOException {
        if (embryo == null) { throw new NullPointerException(); }
        if (embryo.getId() == null) { throw new NullPointerException(); }
        
        putImpl(con, embryo);
    }    
    
    private void putImpl(PartakeConnection con, Event embryo) throws DAOException {
        if (embryo == null) { throw new NullPointerException(); }
        if (embryo.getId() == null) { throw new NullPointerException(); }
        
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            // addToEvents を最後にする。(Event Master Table に最後に入るようにする。)
            // これで途中で死んでも master に入ってないのでデータがないように見える。
            // (RecentEvents と eventsByOwner で、eventId から event データが取れなかった場合は無視するようにすればよい。)
            
            long time = ccon.getAcquiredTime();
            addToEventsByOwner(ccon.getClient(), embryo.getId(), embryo.getOwnerId(), time);
            addEventImpl(ccon.getClient(), embryo, time);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
   
    @Override
    public void remove(PartakeConnection con, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            removeImpl(ccon.getClient(), eventId, ccon.getAcquiredTime());
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

    private void addEventImpl(Client client, Event embryo, long time) throws Exception {
        String key = EVENTS_PREFIX + embryo.getId();

        List<Mutation> mutations = new ArrayList<Mutation>(); 

        mutations.add(createMutation("title", embryo.getTitle(), time));
        mutations.add(createMutation("shortId", embryo.getShortId(), time));
        mutations.add(createMutation("summary", embryo.getSummary(), time));
        mutations.add(createMutation("category", embryo.getCategory(), time));
        mutations.add(createMutation("beginDate", Util.getTimeString(embryo.getBeginDate()), time));
        mutations.add(createMutation("endDate", embryo.getEndDate(), time));
        mutations.add(createMutation("deadline", embryo.getDeadline(), time));
        mutations.add(createMutation("capacity", String.valueOf(embryo.getCapacity()), time));
        mutations.add(createMutation("url", embryo.getUrl(), time));
        mutations.add(createMutation("place", embryo.getPlace(), time));
        mutations.add(createMutation("address", embryo.getAddress(), time));
        mutations.add(createMutation("description", embryo.getDescription(), time));
        mutations.add(createMutation("hashtag", embryo.getHashTag(), time));
        mutations.add(createMutation("owner", embryo.getOwnerId(), time));
        mutations.add(createMutation("managers", embryo.getManagerScreenNames(), time));
	    mutations.add(createMutation("foreImageId", embryo.getForeImageId(), time));
        mutations.add(createMutation("backImageId", embryo.getBackImageId(), time));
        mutations.add(createColumnMutation("secret", embryo.isPrivate() ? TRUE : FALSE, time)); 
        mutations.add(createMutation("passcode", embryo.getPasscode(), time));
        mutations.add(createMutation("createdAt", embryo.getCreatedAt(), time));
        mutations.add(createMutation("modifiedAt", embryo.getModifiedAt(), time));
        mutations.add(createMutation("deleted", "false", time));
        mutations.add(createMutation("revision", String.valueOf(embryo.getRevision()), time));
        
        client.batch_mutate(EVENTS_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(EVENTS_COLUMNFAMILY, mutations)), EVENTS_CL_W);
    }

//    private boolean appendFeedId(CassandraConnection con, String eventId, String feedId, long time) throws Exception {        
//    	// first, confirm that the event exists.
//        Client client = con.getClient();
//    	Event event = getEventById(con, eventId);
//    	if (event == null) { return false; }
//    	
//    	String key = EVENTS_PREFIX + eventId;
//    	
//    	ColumnPath columnPath = new ColumnPath(EVENTS_COLUMNFAMILY);
//    	columnPath.setColumn(bytes("feedId"));
//    	
//    	byte[] value = bytes(feedId);
//    	
//    	client.insert(EVENTS_KEYSPACE, key, columnPath, value, time, EVENTS_CL_W);
//    	return true;
//    }

    private void addToEventsByOwner(Client client, String id, String ownerId, long time) throws Exception {
        String key = EVENTS_BYOWNER_PREFIX + ownerId;

        ColumnPath columnPath = new ColumnPath(EVENTS_BYOWNER_COLUMNFAMILY);
        columnPath.setColumn(bytes(id));
        byte[] value = new byte[0];
        
        client.insert(EVENTS_BYOWNER_KEYSPACE, key, columnPath, value, time, EVENTS_BYOWNER_CL_W);
    }

    // ----------------------------------------------------------------------
    // retrieval
    
    private List<Event> getEventsByOwnerImpl(CassandraConnection con, String userId) throws Exception {
        Client client = con.getClient();
    	String key = EVENTS_BYOWNER_PREFIX + userId;

    	// TODO: 1000 件以上ある場合はどうしたらいい？ -> DataIterator 使え
        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 1000); 
        predicate.setSlice_range(sliceRange);

        ColumnParent columnParent = new ColumnParent(EVENTS_BYOWNER_COLUMNFAMILY);

        List<ColumnOrSuperColumn> columns =
        	client.get_slice(EVENTS_BYOWNER_KEYSPACE, key, columnParent, predicate, EVENTS_BYOWNER_CL_R);
        
        List<Event> events = new ArrayList<Event>();
        for (ColumnOrSuperColumn result : columns) {
            Column column = result.column;
            if (column == null) { continue; }
            String id = string(column.getName());
            if (id == null) { continue; }
            Event event = getEventImpl(con, id);
            if (event == null) { continue; }
            events.add(event);
        }
        
        Collections.sort(events, Event.getComparatorBeginDateAsc());
        
        return events;
    }
    
    // 削除フラグをたてるのみで、実際には消さないようにする。(このほうが Cassandra が死んでて null だったのか消えたのかの区別が楽)
    private void removeImpl(Client client, String eventId, long time) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        String key = EVENTS_PREFIX + eventId;
        
        ColumnPath columnPath = new ColumnPath(EVENTS_COLUMNFAMILY);
        columnPath.setColumn(bytes("deleted"));
        
        client.insert(EVENTS_KEYSPACE, key, columnPath, TRUE, time, EVENTS_CL_W);
    }    

    private Event getEventImpl(CassandraConnection con, String id) throws Exception {
        String key = EVENTS_PREFIX + id;

        List<ColumnOrSuperColumn> results = getSlice(con.getClient(), EVENTS_KEYSPACE, EVENTS_COLUMNFAMILY, key, EVENTS_CL_R);
        
        if (results == null || results.isEmpty()) { return null; }
        
        // TODO: これは本当にひどいのでなんとかするべき。目が腐るよ！　みちゃだめ！
        Event event = new Event();
        event.setId(id);
        
        int revision = 0;
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            String name = string(column.getName());
            String value = string(column.getValue());
            
            if ("shortId".equals(name)) {
                event.setShortId(value);
            } else if ("title".equals(name)) {
                event.setTitle(value);
            } else if ("summary".equals(name)) {
                event.setSummary(value);
            } else if ("category".equals(name)) {
                event.setCategory(value);
            } else if ("deadline".equals(name)) {
            	event.setDeadline(Util.dateFromTimeString(value));
            } else if ("beginDate".equals(name)) {
                event.setBeginDate(Util.dateFromTimeString(value));
            } else if ("endDate".equals(name)) {
                event.setEndDate(Util.dateFromTimeString(value));
            } else if ("capacity".equals(name)) {
                event.setCapacity(Integer.parseInt(value));
            } else if ("url".equals(name)) {
                event.setUrl(value);
            } else if ("place".equals(name)) {
                event.setPlace(value);
            } else if ("address".equals(name)) {
                event.setAddress(value);
            } else if ("description".equals(name)) {
                event.setDescription(value);
            } else if ("hashtag".equals(name)) {
                event.setHashTag(value);
            } else if ("owner".equals(name)) {
                event.setOwnerId(value);
            } else if ("managers".equals(name)) {
                event.setManagerScreenNames(value);
            } else if ("foreImageId".equals(name)) {
                event.setForeImageId(value);
            } else if ("backImageId".equals(name)) {
                event.setBackImageId(value);
            } else if ("secret".equals(name)) {
                event.setPrivate(Boolean.valueOf(value));
            } else if ("passcode".equals(name)) {
                event.setPasscode(value);
            } else if ("createdAt".equals(name)) {
                event.setCreatedAt(Util.dateFromTimeString(value));
            } else if ("modifiedAt".equals(name)) {
            	event.setModifiedAt(Util.dateFromTimeString(value));
            } else if ("revision".equals(name)) {
            	revision  = Integer.parseInt(value);
            } else if ("deleted".equals(name)) {
                if ("true".equals(value)) { return null; }
                // otherwise, ignore it.
            }
        }
        event.setRevision(revision);
        
        return event.freeze();
    }

	@Override
	public boolean isRemoved(PartakeConnection con, String id) throws DAOException {
        final String key = EVENTS_PREFIX + id;
        final List<ColumnOrSuperColumn> results;
        try {
            results = getSlice(((CassandraConnection)con).getClient(), EVENTS_KEYSPACE, EVENTS_COLUMNFAMILY, key, EVENTS_CL_R);
        } catch (Exception e) {
            throw new DAOException(e);
        }

        if (results == null || results.isEmpty()) {
            throw new IllegalStateException();
        }

        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            String name = string(column.getName());
            String value = string(column.getValue());
            if ("deleted".equals(name)) {
                if ("true".equals(value)) { return true; }
            }
        }

        return false;
	}
}






