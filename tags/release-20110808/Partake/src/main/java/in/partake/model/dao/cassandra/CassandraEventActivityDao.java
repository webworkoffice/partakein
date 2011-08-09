package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;


import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IEventActivityAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventActivity;
import in.partake.util.Util;

public class CassandraEventActivityDao extends CassandraDao implements IEventActivityAccess {

    // MASTER TABLE
    private static final String PREFIX = "eventactivity:id:";
    private static final String KEYSPACE = "Keyspace1";
    private static final String COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel CL_W = ConsistencyLevel.ALL;

    //
    // EA_PREFIX + eventId
    //      <time + id> / id
    private static final String EA_PREFIX = "eventactivity:event:";
    private static final String EA_KEYSPACE = "Keyspace1";
    private static final String EA_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel EA_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel EA_CL_W = ConsistencyLevel.ALL;
    
    public CassandraEventActivityDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    @Override
    public void put(PartakeConnection con, EventActivity t) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            putImpl(ccon, t, ccon.getAcquiredTime());
            putEAImpl(ccon, t, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public EventActivity find(PartakeConnection con, String key) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return findImpl(ccon, key);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            removeImpl(ccon, key, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public DataIterator<EventActivity> getIterator(PartakeConnection con) throws DAOException {
        return getIteratorImpl((CassandraConnection) con,
                new CassandraTableDescription(PREFIX, KEYSPACE, COLUMNFAMILY, CL_R, CL_W),
                this);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        truncateImpl((CassandraConnection) con);
    }

    // ----------------------------------------------------------------------
    
    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return UUID.randomUUID().toString();
    }

    @Override
    public List<EventActivity> findByEventId(PartakeConnection con, String eventId, int length) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return findByEventIdImpl(ccon, eventId, length);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    // ----------------------------------------------------------------------

    private void putImpl(CassandraConnection con, EventActivity t, long time) throws Exception {
        String key = PREFIX + t.getId();

        List<Mutation> mutations = new ArrayList<Mutation>();
        mutations.add(createMutation("eventId", t.getEventId(), time));
        mutations.add(createMutation("title", t.getTitle(), time));
        mutations.add(createMutation("content", t.getContent(), time));
        mutations.add(createMutation("createdAt", t.getCreatedAt(), time));
        mutations.add(createMutation("deleted", "false", time));
        
        con.getClient().batch_mutate(KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(COLUMNFAMILY, mutations)), CL_W);
    }

    private void putEAImpl(CassandraConnection con, EventActivity t, long time) throws Exception {
        String key = EA_PREFIX + t.getEventId();
        
        List<Mutation> mutations = new ArrayList<Mutation>();
        mutations.add(createMutation(Util.getReversedTimeString(t.getCreatedAt()) + t.getId(), t.getId(), time));
        
        con.getClient().batch_mutate(EA_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(EA_COLUMNFAMILY, mutations)), EA_CL_W);        
    }
    
    private void removeImpl(CassandraConnection con, String id, long time) throws Exception {
        String key = PREFIX + id;

        List<Mutation> mutations = new ArrayList<Mutation>();
        mutations.add(createMutation("deleted", "true", time));
        
        con.getClient().batch_mutate(KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(COLUMNFAMILY, mutations)), CL_W);
    }
    
    private EventActivity findImpl(CassandraConnection con, String id) throws Exception {
        String key = PREFIX + id;

        List<ColumnOrSuperColumn> results = getSlice(con.getClient(), KEYSPACE, COLUMNFAMILY, key, CL_R);
        
        if (results == null || results.isEmpty()) { return null; }

        EventActivity activity = new EventActivity();
        activity.setId(id);
        
        for (ColumnOrSuperColumn result : results) {
            Column column = result.getColumn();
            String name = string(column.getName());
            String value = string(column.getValue());

            if ("eventId".equals(name)) {
                activity.setEventId(value);
            } else if ("title".equals(name)) {
                activity.setTitle(value);
            } else if ("content".equals(name)) {
                activity.setContent(value);
            } else if ("createdAt".equals(name)) {
                activity.setCreatedAt(Util.dateFromTimeString(value));
            } else if ("deleted".equals(name)) {
                if ("true".equals(value)) { return null; }
                // otherwise, ignore it.
            }
        }
        
        return activity.freeze();
    }
    
    private List<EventActivity> findByEventIdImpl(CassandraConnection con, String eventId, int length) throws Exception {
        String key = EA_PREFIX + eventId;
    
        List<EventActivity> activities = new ArrayList<EventActivity>();
        
        ColumnIterator it = new ColumnIterator(con, EA_KEYSPACE, key, EA_COLUMNFAMILY, false, EA_CL_R, EA_CL_W);
        while (it.hasNext() && length > 0) {
            ColumnOrSuperColumn cosc = it.next();
            if (cosc == null) { continue; }
            Column column = cosc.getColumn();
            if (column == null) { continue; }
            
            String name = string(column.getName());
            String value = string(column.getValue());
            
            EventActivity activity = this.findImpl(con, value);
            if (!name.equals(Util.getReversedTimeString(activity.getCreatedAt()) + activity.getId())) { continue; }
            
            activities.add(activity);
            --length;
        }
        
        // NOTE: The activities are already sorted by createdAt.
        
        return activities;
    }
}


