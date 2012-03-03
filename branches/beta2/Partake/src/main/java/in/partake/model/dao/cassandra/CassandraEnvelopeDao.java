package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SuperColumn;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IEnvelopeAccess;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.util.Util;

class CassandraEnvelopeDao extends CassandraDao implements IEnvelopeAccess {
    private static final String DIRECTMESSAGE_ENVELOPE_PREFIX = "directmessage:envelope";
    private static final String DIRECTMESSAGE_ENVELOPE_KEYSPACE = "Keyspace1";
    private static final String DIRECTMESSAGE_ENVELOPE_COLUMNFAMILY = "Super1";
    private static final ConsistencyLevel DIRECTMESSAGE_ENVELOPE_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel DIRECTMESSAGE_ENVELOPE_CL_W = ConsistencyLevel.ALL;

    CassandraEnvelopeDao(CassandraDAOFactory factory) {
        super(factory);
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return Util.getTimeString(new Date()) + UUID.randomUUID().toString();
    }

    @Override
    public void put(PartakeConnection con, Envelope envelope) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            putImpl(ccon, envelope, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }                    
    }
    
    @Override
    public Envelope find(PartakeConnection con, String key) throws DAOException {
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
    public DataIterator<Envelope> getIterator(PartakeConnection con) throws DAOException {
        try {
            return getIteratorImpl((CassandraConnection) con);
        } catch (Exception e) {
            throw new DAOException(e);
        }   
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        this.removeAllData((CassandraConnection) con);
    }

    // ----------------------------------------------------------------------

    private void putImpl(CassandraConnection con, Envelope envelope, long time) throws Exception {  
        String key = DIRECTMESSAGE_ENVELOPE_PREFIX;

        List<Mutation> mutations = new ArrayList<Mutation>();
        mutations.add(CassandraDaoUtils.createMutation(new EnvelopeMapper(con, factory).unmap(envelope, time)));

        con.getClient().batch_mutate(DIRECTMESSAGE_ENVELOPE_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(DIRECTMESSAGE_ENVELOPE_COLUMNFAMILY, mutations)), DIRECTMESSAGE_ENVELOPE_CL_W);
    }

    private void removeImpl(CassandraConnection con, String id, long time) throws Exception {  
        String key = DIRECTMESSAGE_ENVELOPE_PREFIX;

        ColumnPath columnPath = new ColumnPath(DIRECTMESSAGE_ENVELOPE_COLUMNFAMILY);
        columnPath.setSuper_column(bytes(id));
        columnPath.setColumn(bytes("deleted"));

        con.getClient().insert(DIRECTMESSAGE_ENVELOPE_KEYSPACE, key, columnPath, bytes("true"), time, DIRECTMESSAGE_ENVELOPE_CL_W);
    }

    private Envelope findImpl(CassandraConnection con, String id) throws Exception {
        String key = DIRECTMESSAGE_ENVELOPE_PREFIX;
        ColumnOrSuperColumn cosc = getSuperColumn(con.getClient(), DIRECTMESSAGE_ENVELOPE_KEYSPACE, DIRECTMESSAGE_ENVELOPE_COLUMNFAMILY, id, key, DIRECTMESSAGE_ENVELOPE_CL_R);
        return new EnvelopeMapper(con, factory).map(cosc);
    }
    
    
    private CassandraColumnDataIterator<Envelope> getIteratorImpl(CassandraConnection con) throws Exception {
        String key = DIRECTMESSAGE_ENVELOPE_PREFIX;

        ColumnIterator iterator = new ColumnIterator(con, DIRECTMESSAGE_ENVELOPE_KEYSPACE, key, DIRECTMESSAGE_ENVELOPE_COLUMNFAMILY,              
                    false, DIRECTMESSAGE_ENVELOPE_CL_R, DIRECTMESSAGE_ENVELOPE_CL_W);

        return new CassandraColumnDataIterator<Envelope>(iterator, new EnvelopeMapper(con, factory)); 
    }
}

class EnvelopeMapper extends ColumnOrSuperColumnMapper<Envelope> {
    
    public EnvelopeMapper(CassandraConnection con, PartakeDAOFactory factory) {
        super(con, factory);
    }
    
   
    @Override
    public Envelope map(ColumnOrSuperColumn cosc) throws DAOException {
        if (cosc == null) { return null; }
        
        Envelope envelope = new Envelope();

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
            } else if ("createdAt".equals(key)) {
                envelope.setCreatedAt(Util.dateFromTimeString(value));
            } else if ("deleted".equals(key)) {
                if ("true".equals(value)) { return null; }
            }
        }

        return envelope.freeze();
    }

    @Override
    public ColumnOrSuperColumn unmap(Envelope envelope, long time) throws DAOException {
        if (envelope == null) { return null; }
        
        SuperColumn superColumn = new SuperColumn();

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
        if (envelope.getLastTriedAt() != null) {
            superColumn.addToColumns(new Column(bytes("lastTriedAt"), bytes(Util.getTimeString(envelope.getLastTriedAt())), time));
        }
        superColumn.addToColumns(new Column(bytes("postingType"), bytes(envelope.getPostingType().toString()), time));
        
        if (envelope.getCreatedAt() != null) {
            superColumn.addToColumns(new Column(bytes("createdAt"), bytes(Util.getTimeString(envelope.getCreatedAt())), time));
        } else {
            // FIXME 必須項目なのでここに来ることはないはず。本来は例外を投げるべきだが潜在的バグでサービス停止になる可能性を考慮、当面はログを出すだけにする
            // LOGGER.warn("createdAt(required) is not setted!", new IllegalStateException());
        }
        
        superColumn.addToColumns(new Column(bytes("deleted"),     bytes("false"), time));

        ColumnOrSuperColumn cosc = new ColumnOrSuperColumn();
        cosc.setSuper_column(superColumn);
        return cosc;
    }
}
