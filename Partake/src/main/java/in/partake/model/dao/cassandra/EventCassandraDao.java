package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.KeyIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.model.dto.User;
import in.partake.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


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
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

// * from id
//      events:id:<event id>
//          <event information>
//
// * from owner id (events whose owner is the user)
//      events:owner:<user id>
//			<event id>/""
//			NOTE: value に特に意味はないので new byte[0] を入れておく
//          TODO: event が消去されていれば消すべき？
//          TODO: event が archived 基準であれば archived に移すべき
//
// * event リストで、begin date で並べたものとかなんとか。
//		archive されていない event で、現在時刻よりも前の event を列挙したい
//		archive されているイベントは触ることが出来ない。
//
// コレより下は実装されていない。イベントが多くなる前に実装する必要がある。
// * archived event by owner
//      events:archived:owner:<user id>:<event id>
//
// * archived event by user
//      events:archived:user:<user id>:<event id>
//

class EventCassandraDao extends CassandraDao implements IEventAccess {
    private static final Logger logger = Logger.getLogger(EventCassandraDao.class);

    
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
    public Event getEventById(PartakeConnection con, String id) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getEventById(ccon, id);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    /* (non-Javadoc)
     * @see in.partake.dao.cassandra.IEventAccess#getEventsByIds(java.util.List)
     */
    @Override
    public List<Event> getEventsByIds(PartakeConnection con, List<String> ids) throws DAOException {
        try {
            List<Event> events = new ArrayList<Event>();
            for (String id : ids) {
                Event event = getEventById(con, id);
                if (event != null) { events.add(event); }
            }
            return events;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    /* (non-Javadoc)
     * @see in.partake.dao.cassandra.IEventAccess#getAllEventKeys()
     */
    @Override
    public KeyIterator getAllEventKeys(PartakeConnection connection) throws DAOException {
        return new CassandraKeyIterator((CassandraConnection) connection, EVENTS_KEYSPACE, EVENTS_PREFIX, EVENTS_COLUMNFAMILY, EVENTS_CL_R);
    }
    

    
    @Override
    public List<Event> getEventsByOwner(PartakeConnection con, User owner) throws DAOException {
        return getEventsByOwner(con, owner.getId());
    }
    
    @Override
    public List<Event> getEventsByOwner(PartakeConnection con, String userId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getEventsByOwnerImpl(ccon, userId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    // id が返却される。
    @Override
    public void addEvent(PartakeConnection con, String eventId, Event embryo) throws DAOException {
        addEventImpl(con, eventId, embryo);
    }    
    
    @Override
    public void addEventAsDemo(PartakeConnection con, Event embryo) throws DAOException {
        addEventImpl(con, "demo", embryo);
    }
    
    // TODO: DAO が仕事しすぎ?
    private String addEventImpl(PartakeConnection con, String eventId, Event embryo) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            // addToEvents を最後にする。(Event Master Table に最後に入るようにする。)
            // これで途中で死んでも master に入ってないのでデータがないように見える。
            // (RecentEvents と eventsByOwner で、eventId から event データが取れなかった場合は無視するようにすればよい。)
            
            long time = ccon.getAcquiredTime();
            addToEventsByOwner(ccon.getClient(), eventId, embryo.getOwnerId(), time);
            addEvent(ccon.getClient(), eventId, embryo, time);
            return eventId;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void updateEvent(PartakeConnection con, Event original, Event embryo) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addEvent(ccon.getClient(), original.getId(), embryo, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void updateEventRevision(PartakeConnection con, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            updateEventRevision(ccon.getClient(), eventId, con.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void removeEvent(PartakeConnection con, Event event) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            removeEvent(ccon.getClient(), event, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public boolean appendFeedId(PartakeConnection con, String eventId, String feedId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return appendFeedId(ccon, eventId, feedId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
        
    // ----------------------------------------------------------------------
    // insertion

    private void addEvent(Client client, String id, Event embryo, long time) throws Exception {
        String key = EVENTS_PREFIX + id;

        List<Mutation> mutations = new ArrayList<Mutation>(); 

        mutations.add(createMutation("id", id, time));
        mutations.add(createMutation("title", embryo.getTitle(), time));
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
        if (embryo.getManagerScreenNames() != null) {
            mutations.add(createMutation("managers", StringUtils.join(embryo.getManagerScreenNames(), ","), time));
        } else {
            mutations.add(createMutation("managers", "", time));
        }
	    mutations.add(createMutation("foreImageId", embryo.getForeImageId(), time));
        mutations.add(createMutation("backImageId", embryo.getBackImageId(), time));
        mutations.add(createColumnMutation("secret", embryo.isPrivate() ? TRUE : FALSE, time)); 
        mutations.add(createMutation("passcode", embryo.getPasscode(), time));
        mutations.add(createMutation("createdAt", embryo.getCreatedAt(), time));
        mutations.add(createMutation("modifiedAt", embryo.getModifiedAt(), time));
        
        client.batch_mutate(EVENTS_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(EVENTS_COLUMNFAMILY, mutations)), EVENTS_CL_W);
    }
    
    private void updateEventRevision(Client client, String eventId, long time) throws Exception {
        String key = EVENTS_PREFIX + eventId;
        
        // This should be performed in the transaction, however, Cassandra does not have the transaction function, 
        // So we calculate the revison here. This will not ensure the value is correct. 
        
        int revision = 0;
        ColumnOrSuperColumn cosc = get(client, EVENTS_KEYSPACE, EVENTS_COLUMNFAMILY, "revision", key, EVENTS_CL_R);
        if (cosc != null && cosc.getColumn() != null) {
            try {
                revision = Integer.parseInt(string(cosc.getColumn().getValue()));
            } catch (NumberFormatException e) {
                logger.warn("Integer.parseInt failed.", e);
            }
        }
        
        revision += 1;
        
        List<Mutation> mutations = new ArrayList<Mutation>();
        mutations.add(createMutation("revision", String.valueOf(revision), time));
        client.batch_mutate(EVENTS_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(EVENTS_COLUMNFAMILY, mutations)), EVENTS_CL_W);
    }

    private boolean appendFeedId(CassandraConnection con, String eventId, String feedId, long time) throws Exception {        
    	// first, confirm that the event exists.
        Client client = con.getClient();
    	Event event = getEventById(con, eventId);
    	if (event == null) { return false; }
    	
    	String key = EVENTS_PREFIX + eventId;
    	
    	ColumnPath columnPath = new ColumnPath(EVENTS_COLUMNFAMILY);
    	columnPath.setColumn(bytes("feedId"));
    	
    	byte[] value = bytes(feedId);
    	
    	client.insert(EVENTS_KEYSPACE, key, columnPath, value, time, EVENTS_CL_W);
    	return true;
    }

    private void addToEventsByOwner(Client client, String id, String ownerId, long time) throws Exception {
        String key = EVENTS_BYOWNER_PREFIX + ownerId;

        ColumnPath columnPath = new ColumnPath(EVENTS_BYOWNER_COLUMNFAMILY);
        columnPath.setColumn(bytes(id));
        byte[] value = new byte[0];
        
        client.insert(EVENTS_BYOWNER_KEYSPACE, key, columnPath, value, time, EVENTS_BYOWNER_CL_W);
    }

    // ----------------------------------------------------------------------
    // retrieval
    
    // TODO: DAO が仕事しすぎ。EventId のリストを返すに止めれば良い．
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
        
        ArrayList<String> ids = new ArrayList<String>();
        for (ColumnOrSuperColumn result : columns) {
            Column column = result.column;
            if (column == null) { continue; }            
            ids.add(string(column.getName()));
        }
        
        List<Event> events = getEventsByIds(con, ids);
        Collections.sort(events, Event.getComparatorBeginDateAsc());
        
        return events;
    }
    
    // 削除フラグをたてるのみで、実際には消さないようにする。(このほうが Cassandra が死んでて null だったのか消えたのかの区別が楽)
    private void removeEvent(Client client, Event event, long time) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        String key = EVENTS_PREFIX + event.getId();
        
        ColumnPath columnPath = new ColumnPath(EVENTS_COLUMNFAMILY);
        columnPath.setColumn(bytes("deleted"));
        
        client.insert(EVENTS_KEYSPACE, key, columnPath, TRUE, time, EVENTS_CL_W);
    }    

    private Event getEventById(CassandraConnection con, String id) throws Exception {
        String key = EVENTS_PREFIX + id;

        List<ColumnOrSuperColumn> results = getSlice(con.getClient(), EVENTS_KEYSPACE, EVENTS_COLUMNFAMILY, key, EVENTS_CL_R);
        
        if (results == null || results.isEmpty()) { return null; }
        
        // TODO: これは本当にひどいのでなんとかするべき。目が腐るよ！　みちゃだめ！
        Event event = new Event();
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            String name = string(column.getName());
            String value = string(column.getValue());
            
            if ("id".equals(name)) {
                event.setId(value);
            } else if ("shortId".equals(name)) {
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
                String[] strs = value.split(",");
                event.setManagerScreenNames(Arrays.asList(strs));
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
                event.setRevision(Integer.parseInt(value));
            } else if ("deleted".equals(name)) {
            	if ("false".equals(value)) {
            		// "false" の場合は無視する
            	} else {
                    // deleted flag が立っている場合、null を返す。
            		return null;
            	}                
            }
        }
        
        // if there is no id, the event must not exist. So we should return null.
        if (event.getId() == null) { return null; }
        
        return event.freeze();
    }
}






