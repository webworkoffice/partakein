package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IDirectMessageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.DirectMessageEnvelope;
import in.partake.model.dto.EventReminderStatus;
import in.partake.model.dto.aux.DirectMessagePostingType;
import in.partake.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.SuperColumn;

/**
 * TABLE 構造
 * 
 * directmessage:id:<message id>
 *     senderId/<user id> (optional) <-- TODO: これ userId にしたほうがよかったかなあ...。
 *     message/<string>
 *     eventId/<event id> (optional)
 *     createdAt/<time string>
 *
 * directmessage:event:<event id>
 *     <time>/<message Id>
 *
 * directmessage:envelope
 *     <envelope id> {
 *         id/<message id>
 *         sender/<user id>
 *         receiver/<user id>
 *         deadline/<date>
 *         numTried/<int>
 *         lastTriedAt/<date string>
 *         postingType/<posting type>
 *     }
 *
 * directmessage:reminder:<event id>
 *     "beforeDeadline"/<date>
 *     "beforeDeadlineHalfday"/<date>
 *     "beforeTheDay"/<date>
 *     
 *
 *         
 * @author shinyak
 *
 */
class DirectMessageCassandraDao extends CassandraDao implements IDirectMessageAccess {
    // private static final Logger logger = Logger.getLogger(DirectMessageCassandraDao.class);
    
    // MASTER
    private static final String DIRECTMESSAGE_PREFIX = "directmessage:id:";
    private static final String DIRECTMESSAGE_KEYSPACE = "Keyspace1";
    private static final String DIRECTMESSAGE_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel DIRECTMESSAGE_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel DIRECTMESSAGE_CL_W = ConsistencyLevel.ALL;
    
    // take from event
    private static final String DIRECTMESSAGE_EVENT_PREFIX = "directmessage:event:";
    private static final String DIRECTMESSAGE_EVENT_KEYSPACE = "Keyspace1";
    private static final String DIRECTMESSAGE_EVENT_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel DIRECTMESSAGE_EVENT_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel DIRECTMESSAGE_EVENT_CL_W = ConsistencyLevel.ALL;

    //
    private static final String DIRECTMESSAGE_ENVELOPE_PREFIX = "directmessage:envelope";
    private static final String DIRECTMESSAGE_ENVELOPE_KEYSPACE = "Keyspace1";
    private static final String DIRECTMESSAGE_ENVELOPE_COLUMNFAMILY = "Super1";
    private static final ConsistencyLevel DIRECTMESSAGE_ENVELOPE_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel DIRECTMESSAGE_ENVELOPE_CL_W = ConsistencyLevel.ALL;
    
    // reminder
    private static final String DIRECTMESSAGES_REMINDER_PREFIX = "directmessage:reminder";
    private static final String DIRECTMESSAGES_REMINDER_KEYSPACE = "Keyspace1";
    private static final String DIRECTMESSAGES_REMINDER_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel DIRECTMESSAGES_REMINDER_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel DIRECTMESSAGES_REMINDER_CL_W = ConsistencyLevel.ALL;

    private static final String DIRECTMESSAGES_REMINDER_KEY_BEFORE_DEADLINE_ONEDAY  = "beforeDeadline";
    private static final String DIRECTMESSAGES_REMINDER_KEY_BEFORE_DEADLINE_HALFDAY = "beforeDeadlineHalfday";
    private static final String DIRECTMESSAGES_REMINDER_KEY_BEFORE_THEDAY           = "beforeTheDay";
    
    public DirectMessageCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public DirectMessage getDirectMessageById(PartakeConnection con, String messageId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getMessageById(ccon.getClient(), messageId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    /**
     * add a message.
     * @param embryo
     * @return message id
     * @throws DAOException
     */
    @Override
    public void addMessage(PartakeConnection con, String messageId, DirectMessage embryo) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addMessage(ccon.getClient(), messageId, embryo, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void addUserMessage(PartakeConnection con, String messageId, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addUserMessage(ccon.getClient(), messageId, eventId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public DataIterator<DirectMessage> getUserMessageIterator(PartakeConnection con, String eventId) throws DAOException {
        try {
            return getUserMessageIteratorImpl((CassandraConnection) con, eventId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    // ----------------------------------------------------------------------
    // Envelope
    
    @Override
    public void sendEnvelope(PartakeConnection con, String messageId, String senderId, String receiverId, Date deadline, DirectMessagePostingType postingType) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addEnvelope(ccon.getClient(), messageId, senderId, receiverId, deadline, postingType, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }                    
    }
    
    @Override
    public DataIterator<DirectMessageEnvelope> getEnvelopeIterator(PartakeConnection con) throws DAOException {
        try {
            return getEnvelopeIteratorImpl((CassandraConnection) con);
        } catch (Exception e) {
            throw new DAOException(e);
        }   
    }
    
    // ----------------------------------------------------------------------
    
    @Override
    public EventReminderStatus getEventReminderStatus(PartakeConnection con, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getEventReminderStatusImpl(ccon.getClient(), eventId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    private EventReminderStatus getEventReminderStatusImpl(Client client, String eventId) throws Exception {
        String key = DIRECTMESSAGES_REMINDER_PREFIX + eventId;
        List<ColumnOrSuperColumn> results = getSlice(client, DIRECTMESSAGES_REMINDER_KEYSPACE, DIRECTMESSAGES_REMINDER_COLUMNFAMILY, key, DIRECTMESSAGES_REMINDER_CL_R);
        
        EventReminderStatus reminderStatus = new EventReminderStatus(eventId);

        // if nodata is stored, we return the default value.
        if (results == null || results.isEmpty()) { return new EventReminderStatus(eventId); }

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
            }
        }
        
        return reminderStatus.freeze();
    }
    
    @Override
    public void updateEventReminderStatus(PartakeConnection con, String eventId, EventReminderStatus embryo) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            updateEventReminderStatusImpl(ccon.getClient(), eventId, embryo, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    private void updateEventReminderStatusImpl(Client client, String eventId, EventReminderStatus embryo, long time) throws Exception {
        String key = DIRECTMESSAGES_REMINDER_PREFIX + eventId;

        List<Mutation> mutations = new ArrayList<Mutation>(); 
        
        mutations.add(createMutation(DIRECTMESSAGES_REMINDER_KEY_BEFORE_DEADLINE_ONEDAY,  embryo.getSentDateOfBeforeDeadlineOneday(),  time)); 
        mutations.add(createMutation(DIRECTMESSAGES_REMINDER_KEY_BEFORE_DEADLINE_HALFDAY, embryo.getSentDateOfBeforeDeadlineHalfday(), time));
        mutations.add(createMutation(DIRECTMESSAGES_REMINDER_KEY_BEFORE_THEDAY,           embryo.getSentDateOfBeforeTheDay(),          time));
        
        client.batch_mutate(DIRECTMESSAGES_REMINDER_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(DIRECTMESSAGES_REMINDER_COLUMNFAMILY, mutations)), DIRECTMESSAGES_REMINDER_CL_W);        
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        this.removeAllData((CassandraConnection) con);
    }
    
    // ----------------------------------------------------------------------
    
    private DirectMessage getMessageById(Client client, String messageId, long time) throws Exception {
        String key = DIRECTMESSAGE_PREFIX + messageId;

        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 100);
        predicate.setSlice_range(sliceRange);

        ColumnParent parent = new ColumnParent(DIRECTMESSAGE_COLUMNFAMILY);
        List<ColumnOrSuperColumn> results = client.get_slice(DIRECTMESSAGE_KEYSPACE, key, parent, predicate, DIRECTMESSAGE_CL_R);

        if (results == null || results.isEmpty()) { return null; }
        
        String userId = null;
        String message = null;
        String eventId = null;
        Date createdAt = null;
        
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            String name = string(column.getName());
            String value = string(column.getValue());
            
            if ("userId".equals(name) || "senderId".equals(name)) {
                userId = value;
            } else if ("message".equals(name)) {
                message = value;
            } else if ("eventId".equals(name)) {
                eventId = value;
            } else if ("createdAt".equals(name)) {
                createdAt = Util.dateFromTimeString(value);
            }
        }
        
        return new DirectMessage(messageId, userId, message, eventId, createdAt).freeze();
    }
    
    private void addMessage(Client client, String messageId, DirectMessage embryo, long time) throws Exception {
        String key = DIRECTMESSAGE_PREFIX + messageId;

        List<Mutation> mutations = new ArrayList<Mutation>(); 

        mutations.add(createMutation("userId", embryo.getUserId(), time));
        mutations.add(createMutation("message", embryo.getMessage(), time));
        mutations.add(createMutation("eventId", embryo.getEventId(), time));
        mutations.add(createMutation("createdAt", Util.getTimeString(time), time));
        
        
        client.batch_mutate(DIRECTMESSAGE_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(DIRECTMESSAGE_COLUMNFAMILY, mutations)), DIRECTMESSAGE_CL_W);
    }
    
    private void addUserMessage(Client client, String messageId, String eventId, long time) throws Exception {
        String key = DIRECTMESSAGE_EVENT_PREFIX + eventId;
        
        List<Mutation> mutations = new ArrayList<Mutation>(); 
        mutations.add(createMutation(Util.getTimeString(time), messageId, time));

        client.batch_mutate(DIRECTMESSAGE_EVENT_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(DIRECTMESSAGE_EVENT_COLUMNFAMILY, mutations)), DIRECTMESSAGE_EVENT_CL_W);
    }
    
    private CassandraDataIterator<DirectMessage> getUserMessageIteratorImpl(CassandraConnection con, String eventId) throws Exception {
        String key = DIRECTMESSAGE_EVENT_PREFIX + eventId;

        ColumnIterator iterator = 
            new ColumnIterator(con, factory, DIRECTMESSAGE_EVENT_KEYSPACE, key, DIRECTMESSAGE_EVENT_COLUMNFAMILY,
                            true, DIRECTMESSAGE_EVENT_CL_R, DIRECTMESSAGE_EVENT_CL_W);

        return new CassandraDataIterator<DirectMessage>(iterator, new ColumnOrSuperColumnMapper<DirectMessage>(con, factory) {
            @Override
            public DirectMessage map(ColumnOrSuperColumn cosc) throws DAOException {
                Column column = cosc.getColumn();
                String messageId = string(column.getValue());
                
                return factory.getDirectMessageAccess().getDirectMessageById(connection, messageId);               
            }
            
            public ColumnOrSuperColumn unmap(DirectMessage t) throws DAOException {
                throw new UnsupportedOperationException();
            };
        });

    }

    // ----------------------------------------------------------------------

    private CassandraDataIterator<DirectMessageEnvelope> getEnvelopeIteratorImpl(CassandraConnection con) throws Exception {
        String key = DIRECTMESSAGE_ENVELOPE_PREFIX;

        ColumnIterator iterator = 
            new ColumnIterator(con, factory, DIRECTMESSAGE_ENVELOPE_KEYSPACE, key, DIRECTMESSAGE_ENVELOPE_COLUMNFAMILY, 
                            false, DIRECTMESSAGE_ENVELOPE_CL_R, DIRECTMESSAGE_ENVELOPE_CL_W);
        
        return new CassandraDataIterator<DirectMessageEnvelope>(iterator, new ColumnOrSuperColumnMapper<DirectMessageEnvelope>(con, factory) {
            @Override
            public DirectMessageEnvelope map(ColumnOrSuperColumn cosc) throws DAOException {
                DirectMessageEnvelope envelope = new DirectMessageEnvelope();
                
                SuperColumn superColumn = cosc.getSuper_column();
                envelope.setEnvelopeId(string(superColumn.getName()));
                for (Column column : superColumn.getColumns()) {
                    String key = string(column.getName());
                    String value = string(column.getValue());
                    
                    if ("messageId".equals(key)) {
                        envelope.setMessageId(value);
                    } else if ("senderId".equals(key)) {
                        envelope.setSenderId(value);
                    } else if ("receiverId".equals(key)) {
                        envelope.setReceiverId(value);
                    } else if ("deadline".equals(key)) {
                        envelope.setDeadline(Util.dateFromTimeString(value));
                    } else if ("numTried".equals(key)) {
                        envelope.setNumTried(Integer.parseInt(value));
                    } else if ("lastTriedAt".equals(key)) {
                        envelope.setLastTriedAt(Util.dateFromTimeString(value));
                    } else if ("postingType".equals(key)) {
                        envelope.setPostingType(DirectMessagePostingType.valueOf(value));
                    } 
                }
                
                return envelope;
            }
            
            @Override
            public ColumnOrSuperColumn unmap(DirectMessageEnvelope envelope) throws DAOException {
                SuperColumn superColumn = new SuperColumn();
                long time = new Date().getTime();
                
                superColumn.setName(bytes(envelope.getEnvelopeId()));
                
                superColumn.addToColumns(new Column(bytes("messageId"),   bytes(envelope.getMessageId()), time));
                if (envelope.getReceiverId() != null) {
                    superColumn.addToColumns(new Column(bytes("receiverId"),  bytes(envelope.getReceiverId()), time));
                }
                if (envelope.getSenderId() != null) {
                    superColumn.addToColumns(new Column(bytes("senderId"),    bytes(envelope.getSenderId()), time));
                }
                if (envelope.getDeadline() != null) {
                    superColumn.addToColumns(new Column(bytes("deadline"),    bytes(Util.getTimeString(envelope.getDeadline())), time));
                }
                superColumn.addToColumns(new Column(bytes("numTried"),    bytes(String.valueOf(envelope.getNumTried())), time));
                superColumn.addToColumns(new Column(bytes("lastTriedAt"), bytes(Util.getTimeString(envelope.getLastTriedAt())), time));
                superColumn.addToColumns(new Column(bytes("postingType"), bytes(envelope.getPostingType().toString()), time));
                
                ColumnOrSuperColumn cosc = new ColumnOrSuperColumn();
                cosc.setSuper_column(superColumn);
                return cosc;
            }
        });
    }
                     
    
    private void addEnvelope(Client client, String messageId, String senderId, String receiverId,
                    Date deadline, DirectMessagePostingType postingType, long time) throws Exception {  
        String key = DIRECTMESSAGE_ENVELOPE_PREFIX;
        
        Map<String, List<ColumnOrSuperColumn>> cfmap = new HashMap<String, List<ColumnOrSuperColumn>>();
        List<ColumnOrSuperColumn> columns = new ArrayList<ColumnOrSuperColumn>();
                
        {
            SuperColumn superColumn = new SuperColumn();
            superColumn.setName(bytes(time + ":" + messageId + ":" + receiverId));
            
            superColumn.addToColumns(new Column(bytes("messageId"), bytes(messageId), time));
            if (senderId != null) {
                superColumn.addToColumns(new Column(bytes("senderId"), bytes(senderId), time));
            }
            if (receiverId != null) {
                superColumn.addToColumns(new Column(bytes("receiverId"), bytes(receiverId), time));
            }
            if (deadline != null) {
                superColumn.addToColumns(new Column(bytes("deadline"), bytes(Util.getTimeString(deadline)), time));
            }
            superColumn.addToColumns(new Column(bytes("numTried"), bytes("0"), time));
            superColumn.addToColumns(new Column(bytes("lastTriedAt"), bytes(Util.getTimeString(0)), time));
            superColumn.addToColumns(new Column(bytes("postingType"), bytes(postingType.toString()), time));
            
            columns.add(new ColumnOrSuperColumn().setSuper_column(superColumn));
        }
        
        cfmap.put(DIRECTMESSAGE_ENVELOPE_COLUMNFAMILY, columns);
        
        client.batch_insert(DIRECTMESSAGE_ENVELOPE_KEYSPACE, key, cfmap, DIRECTMESSAGE_ENVELOPE_CL_W);
    }

}
