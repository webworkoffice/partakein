package in.partake.model.dao.cassandra;

import java.util.List;
import java.util.UUID;

import org.apache.cassandra.thrift.ConsistencyLevel;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IQuestionnaireAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Questionnaire;

public class CassandraQuestionnaireDao extends CassandraDao implements IQuestionnaireAccess {

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
    public void remove(PartakeConnection con, String key) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            removeImpl(ccon, key, ccon.getAcquiredTime());            
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
        throw new RuntimeException("not implemented yet");
    }

    private void putIndexImpl(CassandraConnection con, Questionnaire q, long time) throws Exception {
        throw new RuntimeException("not implemented yet");  
    }

    private void removeImpl(CassandraConnection con, String id, long time) throws Exception {
        throw new RuntimeException("not implemented yet");
    }

    private Questionnaire findImpl(CassandraConnection con, String id) throws Exception {
        throw new RuntimeException("not implemented yet");
    }

    private List<Questionnaire> findByEventIdImpl(CassandraConnection con, String eventId) throws Exception {
        throw new RuntimeException("not implemented yet");
    }

    private void removeByEventIdImpl(CassandraConnection con, String eventId, long time) throws Exception {
        throw new RuntimeException("not implemented yet");
    }
    
}
