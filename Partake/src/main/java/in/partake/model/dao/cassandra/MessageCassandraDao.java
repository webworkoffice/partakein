package in.partake.model.dao.cassandra;


import static me.prettyprint.cassandra.utils.StringUtils.string;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IMessageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Message;
import in.partake.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;

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
class MessageCassandraDao extends CassandraDao implements IMessageAccess {
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


    

    
    public MessageCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public Message getMessage(PartakeConnection con, String messageId) throws DAOException {
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
    public void addMessage(PartakeConnection con, Message embryo) throws DAOException {
        if (embryo.getId() == null) { throw new NullPointerException(); }
        
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addMessage(ccon.getClient(), embryo, ccon.getAcquiredTime());
            if (embryo.getEventId() != null) {
                addUserMessage(ccon.getClient(), embryo.getId(), embryo.getEventId(), ccon.getAcquiredTime());
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public DataIterator<Message> getMessagesByEventId(PartakeConnection con, String eventId) throws DAOException {
        try {
            return getUserMessageIteratorImpl((CassandraConnection) con, eventId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        this.removeAllData((CassandraConnection) con);
    }
    
    // ----------------------------------------------------------------------
    
    private Message getMessageById(Client client, String messageId, long time) throws Exception {
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
        
        return new Message(messageId, userId, message, eventId, createdAt).freeze();
    }
    
    private void addMessage(Client client, Message embryo, long time) throws Exception {
        String key = DIRECTMESSAGE_PREFIX + embryo.getId();

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
    
    private CassandraColumnDataIterator<Message> getUserMessageIteratorImpl(CassandraConnection con, String eventId) throws Exception {
        String key = DIRECTMESSAGE_EVENT_PREFIX + eventId;

        ColumnIterator iterator = 
            new ColumnIterator(con, factory, DIRECTMESSAGE_EVENT_KEYSPACE, key, DIRECTMESSAGE_EVENT_COLUMNFAMILY,
                            true, DIRECTMESSAGE_EVENT_CL_R, DIRECTMESSAGE_EVENT_CL_W);

        return new CassandraColumnDataIterator<Message>(iterator, new ColumnOrSuperColumnMapper<Message>(con, factory) {
            @Override
            public Message map(ColumnOrSuperColumn cosc) throws DAOException {
                Column column = cosc.getColumn();
                String messageId = string(column.getValue());
                
                return factory.getDirectMessageAccess().getMessage(connection, messageId);               
            }
            
            public ColumnOrSuperColumn unmap(Message t) throws DAOException {
                throw new UnsupportedOperationException();
            };
        });

    }

    // ----------------------------------------------------------------------

                     
    

}
