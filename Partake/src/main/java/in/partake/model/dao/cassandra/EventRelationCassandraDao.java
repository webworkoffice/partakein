package in.partake.model.dao.cassandra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SuperColumn;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IEventRelationAccess;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.pk.EventRelationPK;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

// * from id
//    eventrelation:id:<source event id>
//        <dst eventId> / {
//            required/<true or false>
//            priority/<true or false>
//        }


public class EventRelationCassandraDao extends CassandraDao implements IEventRelationAccess {
    // EVENT MASTER TABLE
    private static final String EVENT_RELATION_PREFIX = "eventrelation:id:";
    private static final String EVENT_RELATION_KEYSPACE = "Keyspace1";
    private static final String EVENT_RELATION_COLUMNFAMILY = "Super1";
    private static final ConsistencyLevel EVENT_RELATION_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel EVENT_RELATION_CL_W = ConsistencyLevel.ALL;

    
    EventRelationCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
	@Override
	public void put(PartakeConnection con, EventRelation relation) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
        	putImpl(ccon, relation, con.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
	}
	
	@Override
	public EventRelation find(PartakeConnection con, EventRelationPK pk) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return findImpl(ccon, pk);
        } catch (Exception e) {
            throw new DAOException(e);
        }
	}
		
	@Override
	public void remove(PartakeConnection con, EventRelationPK pk) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            removeImpl(ccon, pk, con.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
	}
	
	@Override
    public DataIterator<EventRelation> getIterator(PartakeConnection con) throws DAOException {
        return new CassandraKeyColumnDataIterator<EventRelation>((CassandraConnection) con,
                new CassandraTableDescription(EVENT_RELATION_PREFIX, EVENT_RELATION_KEYSPACE, EVENT_RELATION_COLUMNFAMILY, EVENT_RELATION_CL_R, EVENT_RELATION_CL_W),        
                new EventRelationMapper((CassandraConnection) con, factory));
    }

	@Override
	public void truncate(PartakeConnection con) throws DAOException {
	    removeAllData((CassandraConnection) con);
	}
	
    @Override
    public List<EventRelation> findByEventId(PartakeConnection con, String eventId) throws DAOException {       
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getEventRelations(ccon, eventId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void removeByEventId(PartakeConnection con, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            removeByEventIdImpl(ccon.getClient(), eventId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

	// ----------------------------------------------------------------------
	
	private void putImpl(CassandraConnection con, EventRelation relation, long time) throws Exception {
	    String key = EVENT_RELATION_PREFIX + relation.getSrcEventId();
		
		EventRelationMapper mapper = new EventRelationMapper(con, factory);
		ColumnOrSuperColumn cosc = mapper.unmap(relation, time);
		
		List<Mutation> mutations = new ArrayList<Mutation>();
		mutations.add(new Mutation().setColumn_or_supercolumn(cosc));
	    
	    con.getClient().batch_mutate(EVENT_RELATION_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(EVENT_RELATION_COLUMNFAMILY, mutations)), EVENT_RELATION_CL_W);
	}	
	
	private EventRelation findImpl(CassandraConnection con, EventRelationPK pk) throws Exception {
	    String key = EVENT_RELATION_PREFIX + pk.getSrcEventId();
        ColumnPath columnPath = new ColumnPath(EVENT_RELATION_COLUMNFAMILY);
        columnPath.setSuper_column(bytes(pk.getDstEventId()));

        try {
            ColumnOrSuperColumn cosc = con.getClient().get(EVENT_RELATION_KEYSPACE, key, columnPath, EVENT_RELATION_CL_R);
            return new EventRelationMapper(con, factory).map(cosc, pk.getSrcEventId());
        } catch (NotFoundException e) {
            return null;
        }
	}
	
	private void removeImpl(CassandraConnection con, EventRelationPK pk, long time) throws Exception {
	    String key = EVENT_RELATION_PREFIX + pk.getSrcEventId();
        ColumnPath columnPath = new ColumnPath(EVENT_RELATION_COLUMNFAMILY);
        columnPath.setSuper_column(bytes(pk.getDstEventId()));
        
        con.getClient().remove(EVENT_RELATION_KEYSPACE, key, columnPath, time, EVENT_RELATION_CL_W);
	}
	
	private List<EventRelation> getEventRelations(CassandraConnection con, String eventId) throws Exception {
        String key = EVENT_RELATION_PREFIX + eventId;
        
        ArrayList<EventRelation> relations = new ArrayList<EventRelation>();
        
        ColumnIterator it = new ColumnIterator(con, EVENT_RELATION_KEYSPACE, key, EVENT_RELATION_COLUMNFAMILY, false, EVENT_RELATION_CL_R, EVENT_RELATION_CL_W);
        EventRelationMapper mapper = new EventRelationMapper(con, factory);
        while (it.hasNext()) {
            ColumnOrSuperColumn cosc = it.next();
            EventRelation rel = mapper.map(cosc, eventId);
            relations.add(rel);
        }

        return relations;       
    }
	   
	private void removeByEventIdImpl(Client client, String eventId, long time) throws Exception {
	    String key = EVENT_RELATION_PREFIX + eventId;
	    ColumnPath columnPath = new ColumnPath(EVENT_RELATION_COLUMNFAMILY);
	    client.remove(EVENT_RELATION_KEYSPACE, key, columnPath, time, EVENT_RELATION_CL_W);
	}
}

class EventRelationMapper extends ColumnOrSuperColumnKeyMapper<EventRelation> {
 
    public EventRelationMapper(CassandraConnection connection, PartakeDAOFactory factory) {
        super(connection, factory);
    }
    
    @Override
    public EventRelation map(ColumnOrSuperColumn cosc, String srcEventId) throws DAOException {
        SuperColumn superColumn = cosc.getSuper_column();
        String dstEventId = string(superColumn.getName());
        
        EventRelation relation = new EventRelation();
        relation.setSrcEventId(srcEventId);
        relation.setDstEventId(dstEventId);
        
        for (Column column : superColumn.getColumns()) {
            String name = string(column.getName());
            if ("required".equals(name)) {
                relation.setRequired("true".equals(string(column.getValue())));
            } else if ("priority".equals(name)) {
                relation.setPriority("true".equals(string(column.getValue())));
            }
        }

        return relation.freeze();
    }
    
    @Override
    public ColumnOrSuperColumn unmap(EventRelation relation, long time) throws DAOException {
        SuperColumn superColumn = new SuperColumn();
        superColumn.setName(bytes(relation.getDstEventId()));
        superColumn.addToColumns(new Column(bytes("required"), relation.isRequired() ? CassandraDao.TRUE : CassandraDao.FALSE, time));
        superColumn.addToColumns(new Column(bytes("priority"), relation.hasPriority() ? CassandraDao.TRUE : CassandraDao.FALSE, time));

        ColumnOrSuperColumn cosc = new ColumnOrSuperColumn().setSuper_column(superColumn);
        return cosc;
    }
}
