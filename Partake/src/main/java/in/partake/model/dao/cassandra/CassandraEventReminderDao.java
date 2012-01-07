package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.Cassandra.Client;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventReminderAccess;
import in.partake.model.dto.EventReminder;
import in.partake.util.Util;


class CassandraEventReminderDao extends CassandraDao implements IEventReminderAccess {
    // reminder
    private static final String DIRECTMESSAGES_REMINDER_PREFIX = "directmessage:reminder";
    private static final String DIRECTMESSAGES_REMINDER_KEYSPACE = "Keyspace1";
    private static final String DIRECTMESSAGES_REMINDER_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel DIRECTMESSAGES_REMINDER_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel DIRECTMESSAGES_REMINDER_CL_W = ConsistencyLevel.ALL;

    private static final String DIRECTMESSAGES_REMINDER_KEY_BEFORE_DEADLINE_ONEDAY  = "beforeDeadline";
    private static final String DIRECTMESSAGES_REMINDER_KEY_BEFORE_DEADLINE_HALFDAY = "beforeDeadlineHalfday";
    private static final String DIRECTMESSAGES_REMINDER_KEY_BEFORE_THEDAY           = "beforeTheDay";
    private static final String DELETED = "deleted";
    
    public CassandraEventReminderDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    @Override
    public EventReminder find(PartakeConnection con, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return findImpl(ccon.getClient(), eventId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void put(PartakeConnection con, EventReminder embryo) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            putImpl(ccon.getClient(), embryo, ccon.getAcquiredTime());
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
    public DataIterator<EventReminder> getIterator(PartakeConnection con) throws DAOException {
        return getIteratorImpl((CassandraConnection) con, 
                new CassandraTableDescription(DIRECTMESSAGES_REMINDER_PREFIX, DIRECTMESSAGES_REMINDER_KEYSPACE, DIRECTMESSAGES_REMINDER_COLUMNFAMILY, DIRECTMESSAGES_REMINDER_CL_R, DIRECTMESSAGES_REMINDER_CL_W),
                this);
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        this.removeAllData((CassandraConnection) con);
    }

    
    private EventReminder findImpl(Client client, String eventId) throws Exception {
        String key = DIRECTMESSAGES_REMINDER_PREFIX + eventId;
        List<ColumnOrSuperColumn> results = getSlice(client, DIRECTMESSAGES_REMINDER_KEYSPACE, DIRECTMESSAGES_REMINDER_COLUMNFAMILY, key, DIRECTMESSAGES_REMINDER_CL_R);
        
        EventReminder reminderStatus = new EventReminder(eventId);

        // if nodata is stored, we return the default value.
        if (results == null || results.isEmpty()) { return null; }

        for (ColumnOrSuperColumn cosc : results) {
            Column column = cosc.getColumn();
            if (column == null) { continue; }
            
            String name  = string(column.getName());
            String value = string(column.getValue());
            
            if (DIRECTMESSAGES_REMINDER_KEY_BEFORE_DEADLINE_ONEDAY.equals(name)) {
                reminderStatus.setSentDateOfBeforeDeadlineOneday(Util.dateFromTimeString(value));
            } else if (DIRECTMESSAGES_REMINDER_KEY_BEFORE_DEADLINE_HALFDAY.equals(name)) {
                reminderStatus.setSentDateOfBeforeDeadlineHalfday(Util.dateFromTimeString(value));
            } else if (DIRECTMESSAGES_REMINDER_KEY_BEFORE_THEDAY.equals(name)) {
                reminderStatus.setSentDateOfBeforeTheDay(Util.dateFromTimeString(value));
            } else if (DELETED.equals(name)) {
                if ("true".equals(value)) { return null; }
            }
        }
        
        return reminderStatus.freeze();
    }
    
    private void putImpl(Client client, EventReminder embryo, long time) throws Exception {
        String key = DIRECTMESSAGES_REMINDER_PREFIX + embryo.getEventId();

        List<Mutation> mutations = new ArrayList<Mutation>(); 
        
        mutations.add(createMutation(DIRECTMESSAGES_REMINDER_KEY_BEFORE_DEADLINE_ONEDAY,  embryo.getSentDateOfBeforeDeadlineOneday(),  time)); 
        mutations.add(createMutation(DIRECTMESSAGES_REMINDER_KEY_BEFORE_DEADLINE_HALFDAY, embryo.getSentDateOfBeforeDeadlineHalfday(), time));
        mutations.add(createMutation(DIRECTMESSAGES_REMINDER_KEY_BEFORE_THEDAY,           embryo.getSentDateOfBeforeTheDay(),          time));
        mutations.add(createMutation(DELETED,                                             "false",                                     time));
        
        client.batch_mutate(DIRECTMESSAGES_REMINDER_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(DIRECTMESSAGES_REMINDER_COLUMNFAMILY, mutations)), DIRECTMESSAGES_REMINDER_CL_W);        
    }

    private void removeImpl(Client client, String eventId, long time) throws Exception {
        String key = DIRECTMESSAGES_REMINDER_PREFIX + eventId;

        List<Mutation> mutations = new ArrayList<Mutation>(); 
        mutations.add(createMutation(DELETED, "true", time)); 
        
        client.batch_mutate(DIRECTMESSAGES_REMINDER_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(DIRECTMESSAGES_REMINDER_COLUMNFAMILY, mutations)), DIRECTMESSAGES_REMINDER_CL_W);        
    }
}
