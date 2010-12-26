package in.partake.model.dao.cassandra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SuperColumn;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEventRelationAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventRelation;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

// * from id
//    eventrelation:id:<source event id>
//        eventId / {
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
	public List<EventRelation> getEventRelations(PartakeConnection con, String eventId) throws DAOException {		
        CassandraConnection ccon = (CassandraConnection) con;
        try {
        	return getEventRelations(ccon.getClient(), eventId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
	}
	
	@Override
	public void setEventRelations(PartakeConnection con, String eventId, List<EventRelation> relations) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
        	setEventRelations(ccon.getClient(), eventId, relations, con.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
	}
	
	// ----------------------------------------------------------------------
	
	private List<EventRelation> getEventRelations(Client client, String eventId) throws Exception {
		String key = EVENT_RELATION_PREFIX + eventId;
		List<ColumnOrSuperColumn> coscs = getSlice(client, EVENT_RELATION_KEYSPACE, EVENT_RELATION_COLUMNFAMILY, key, EVENT_RELATION_CL_R);
		List<EventRelation> result = new ArrayList<EventRelation>();
		
		EventRelationMapper mapper = new EventRelationMapper();
		for (ColumnOrSuperColumn cosc : coscs) {
			result.add(mapper.unmap(cosc));
		}
		
		return result;
	}
	
	public void setEventRelations(Client client, String eventId, List<EventRelation> relations, long time) throws Exception {
		String key = EVENT_RELATION_PREFIX + eventId;
		List<Mutation> mutations = new ArrayList<Mutation>();

		EventRelationMapper mapper = new EventRelationMapper();
		for (EventRelation relation : relations) {
			mutations.add(mapper.map(relation, time));
		}
	    
	    client.batch_mutate(EVENT_RELATION_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(EVENT_RELATION_COLUMNFAMILY, mutations)), EVENT_RELATION_CL_W);
	}	
}

class EventRelationMapper {
    
	public Mutation map(EventRelation relation, long time) {
		SuperColumn superColumn = new SuperColumn();
		superColumn.setName(bytes(relation.getEventId()));
		superColumn.addToColumns(new Column(bytes("required"), relation.isRequired() ? CassandraDao.TRUE : CassandraDao.FALSE, time));
		superColumn.addToColumns(new Column(bytes("priority"), relation.hasPriority() ? CassandraDao.TRUE : CassandraDao.FALSE, time));

		ColumnOrSuperColumn cosc = new ColumnOrSuperColumn().setSuper_column(superColumn);
		return new Mutation().setColumn_or_supercolumn(cosc);
	}
	
	public EventRelation unmap(ColumnOrSuperColumn cosc) {
		SuperColumn superColumn = cosc.getSuper_column();
		String eventId = string(superColumn.getName());
		
		EventRelation relation = new EventRelation();
		relation.setEventId(eventId);
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
}
