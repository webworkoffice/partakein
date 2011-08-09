package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IQuestionnaireAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Questionnaire;
import in.partake.model.dto.auxiliary.QuestionnaireType;

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
import org.apache.log4j.Logger;
import org.apache.thrift.TException;

public class CassandraQuestionnaireDao extends CassandraDao implements IQuestionnaireAccess {
	private static final Logger LOGGER = Logger.getLogger(CassandraQuestionnaireDao.class);

    // MASTER TABLE
    private static final String PREFIX = "questionnaires:id:";
    private static final String KEYSPACE = "Keyspace1";
    private static final String COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel CL_W = ConsistencyLevel.ALL;

    // INDEX TABLE
    private static final String EVENTINDEX_PREFIX = "questionnaires:event:";
    private static final String EVENTINDEX_KEYSPACE = "Keyspace1";
    private static final String EVENTINDEX_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel EVENTINDEX_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel EVENTINDEX_CL_W = ConsistencyLevel.ALL;
    
    CassandraQuestionnaireDao(CassandraDAOFactory factory) {
        super(factory);
    }

    @Override
    public void put(PartakeConnection con, Questionnaire t) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            putIndexImpl(ccon, t, ccon.getAcquiredTime());
            putImpl(ccon, t, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Questionnaire find(PartakeConnection con, String key) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return findImpl(ccon, key);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            Questionnaire q = find(con, id);
            if (q == null) { return; }
            removeImpl(ccon, id, ccon.getAcquiredTime());
            removeIndexImpl(ccon, id, q.getEventId(), ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public DataIterator<Questionnaire> getIterator(PartakeConnection con) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        return this.getIteratorImpl(ccon, new CassandraTableDescription(PREFIX, KEYSPACE, COLUMNFAMILY, CL_R, CL_W), this);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        this.truncateImpl((CassandraConnection) con);
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return UUID.randomUUID().toString();
    }

    @Override
    public List<Questionnaire> findQuestionnairesByEventId(PartakeConnection con, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return findByEventIdImpl(ccon, eventId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void removeByEventId(PartakeConnection con, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            removeByEventIdImpl(ccon, eventId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    // ----------------------------------------------------------------------

    private void putImpl(CassandraConnection con, Questionnaire q, long time) throws Exception {
        Client client = con.getClient();
        String key = PREFIX + q.getId();
        List<Mutation> mutations = new ArrayList<Mutation>(); 

        mutations.add(createMutation("answerTexts", q.getAnswerTexts(), time));
        mutations.add(createMutation("eventId", q.getEventId(), time));
        mutations.add(createMutation("question", q.getQuestion(), time));
        mutations.add(createMutation("questionNo", Integer.toString(q.getQuestionNo()), time));
        mutations.add(createMutation("type", q.getType().toString(), time));
        mutations.add(createMutation("deleted", "false", time));

        client.batch_mutate(KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(COLUMNFAMILY, mutations)), CL_W);
    }

    private void putIndexImpl(CassandraConnection con, Questionnaire q, long time) throws Exception {
        Client client = con.getClient();
        String key = EVENTINDEX_PREFIX + q.getEventId();
        ColumnPath columnPath = new ColumnPath(EVENTINDEX_COLUMNFAMILY);
        columnPath.setColumn(bytes(q.getId()));
        byte[] value = new byte[0];

        client.insert(EVENTINDEX_KEYSPACE, key, columnPath, value, time, EVENTINDEX_CL_W);
    }

    private void removeImpl(CassandraConnection con, String id, long time) throws Exception {
        String key = PREFIX + id;

        ColumnPath columnPath = new ColumnPath(COLUMNFAMILY);
        columnPath.setColumn(bytes("deleted"));

        con.getClient().insert(KEYSPACE, key, columnPath, TRUE, time, CL_W);
    }

    private void removeIndexImpl(CassandraConnection con, String id, String eventId, long time) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        String key = EVENTINDEX_PREFIX + eventId;
        ColumnPath columnPath = new ColumnPath(EVENTINDEX_COLUMNFAMILY);
        columnPath.setColumn(bytes(id));
        byte[] value = bytes("removed");

        con.getClient().insert(EVENTINDEX_KEYSPACE, key, columnPath, value, time, EVENTINDEX_CL_W);
    }

    private Questionnaire findImpl(CassandraConnection con, String id) throws Exception {
        String key = PREFIX + id;

        List<ColumnOrSuperColumn> results = getSlice(con.getClient(), KEYSPACE, COLUMNFAMILY, key, CL_R);

        if (results == null || results.isEmpty()) { return null; }
        Questionnaire q = new Questionnaire();
        q.setId(id);

        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            String name = string(column.getName());
            String value = string(column.getValue());

            if ("answerTexts".equals(name)) {
                q.setAnswerTexts(value);
            } else if ("eventId".equals(name)) {
                q.setEventId(value);
            } else if ("question".equals(name)) {
                q.setQuestion(value);
            } else if ("questionNo".equals(name)) {
                q.setQuestionNo(Integer.parseInt(value));
            } else if ("type".equals(name)) {
                q.setType(QuestionnaireType.valueOf(value));
            } else if ("deleted".equals(name)) {
                if ("true".equals(value)) { return null; }
            } else {
                LOGGER.warn(String.format("unknown column: name=%s, value=%s", name, value));
            }
        }

        return q.freeze();
    }

    private List<Questionnaire> findByEventIdImpl(CassandraConnection con, String eventId) throws Exception {
        Client client = con.getClient();
        String key = EVENTINDEX_PREFIX + eventId;
        SlicePredicate predicate = new SlicePredicate();

        // TODO 1000件以上に対応
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 1000); 
        predicate.setSlice_range(sliceRange);

        ColumnParent columnParent = new ColumnParent(EVENTINDEX_COLUMNFAMILY);

        List<ColumnOrSuperColumn> columns =
                client.get_slice(EVENTINDEX_KEYSPACE, key, columnParent, predicate, EVENTINDEX_CL_R);

        List<Questionnaire> events = new ArrayList<Questionnaire>();
        for (ColumnOrSuperColumn result : columns) {
            Column column = result.column;
            if (column == null) { continue; }
            String id = string(column.getName());
            if (id == null) { continue; }
            String value = string(column.getValue());
            if ("deleted".equals(value)) { continue; }
            Questionnaire q = find(con, id);
            if (q == null) { continue; }
            events.add(q);
        }

        Collections.sort(events, Questionnaire.getComparatorQuestionNoAsc());
        return events;
    }

    private void removeByEventIdImpl(CassandraConnection con, String eventId, long time) throws Exception {
        for (Questionnaire q : findByEventIdImpl(con, eventId)) {
            removeImpl(con, q.getId() , time);
        }
    }
}
