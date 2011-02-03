package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.thrift.Cassandra.Client;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IEnvelopeAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.util.Util;

class CassandraEnvelopeDao extends CassandraDao implements IEnvelopeAccess {

    //
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
    public void enqueueEnvelope(PartakeConnection con, Envelope envelope) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addEnvelope(ccon.getClient(), envelope, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }                    
    }

    @Override
    public DataIterator<Envelope> getEnvelopeIterator(PartakeConnection con) throws DAOException {
        try {
            return getEnvelopeIteratorImpl((CassandraConnection) con);
        } catch (Exception e) {
            throw new DAOException(e);
        }   
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        this.removeAllData((CassandraConnection) con);
    }

    //

    private void addEnvelope(Client client, Envelope envelope, long time) throws Exception {  
        String key = DIRECTMESSAGE_ENVELOPE_PREFIX;

        Map<String, List<ColumnOrSuperColumn>> cfmap = new HashMap<String, List<ColumnOrSuperColumn>>();
        List<ColumnOrSuperColumn> columns = new ArrayList<ColumnOrSuperColumn>();

        {
            SuperColumn superColumn = new SuperColumn();
            superColumn.setName(bytes(envelope.getEnvelopeId()));

            superColumn.addToColumns(new Column(bytes("messageId"), bytes(envelope.getMessageId()), time));
            if (envelope.getSenderId() != null) {
                superColumn.addToColumns(new Column(bytes("senderId"), bytes(envelope.getSenderId()), time));
            }
            if (envelope.getReceiverId() != null) {
                superColumn.addToColumns(new Column(bytes("receiverId"), bytes(envelope.getReceiverId()), time));
            }
            if (envelope.getDeadline() != null) {
                superColumn.addToColumns(new Column(bytes("deadline"), bytes(Util.getTimeString(envelope.getDeadline())), time));
            }
            superColumn.addToColumns(new Column(bytes("numTried"), bytes(String.valueOf(envelope.getNumTried())), time));
            superColumn.addToColumns(new Column(bytes("lastTriedAt"), bytes(Util.getTimeString(envelope.getLastTriedAt())), time));
            superColumn.addToColumns(new Column(bytes("postingType"), bytes(envelope.getPostingType().toString()), time));

            columns.add(new ColumnOrSuperColumn().setSuper_column(superColumn));
        }

        cfmap.put(DIRECTMESSAGE_ENVELOPE_COLUMNFAMILY, columns);

        client.batch_insert(DIRECTMESSAGE_ENVELOPE_KEYSPACE, key, cfmap, DIRECTMESSAGE_ENVELOPE_CL_W);
    }

    private CassandraColumnDataIterator<Envelope> getEnvelopeIteratorImpl(CassandraConnection con) throws Exception {
        String key = DIRECTMESSAGE_ENVELOPE_PREFIX;

        ColumnIterator iterator = 
            new ColumnIterator(con, factory, DIRECTMESSAGE_ENVELOPE_KEYSPACE, key, DIRECTMESSAGE_ENVELOPE_COLUMNFAMILY, 
                    false, DIRECTMESSAGE_ENVELOPE_CL_R, DIRECTMESSAGE_ENVELOPE_CL_W);

        return new CassandraColumnDataIterator<Envelope>(iterator, new ColumnOrSuperColumnMapper<Envelope>(con, factory) {
            @Override
            public Envelope map(ColumnOrSuperColumn cosc) throws DAOException {
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
                    } 
                }

                return envelope;
            }

            @Override
            public ColumnOrSuperColumn unmap(Envelope envelope) throws DAOException {
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


}
