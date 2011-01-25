package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.string;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.ICalendarLinkageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.CalendarLinkage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.Cassandra.Client;

class CalendarLinkageCassandraDao extends CassandraDao implements ICalendarLinkageAccess {
    // MASTER TABLE
    private static final String CALENDAR_PREFIX = "calendar:id:";
    private static final String CALENDAR_KEYSPACE = "Keyspace1";
    private static final String CALENDAR_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel CALENDAR_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel CALENDAR_CL_W = ConsistencyLevel.ALL;

    CalendarLinkageCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    @Override
    public String getFreshCalendarId(PartakeConnection con) throws DAOException {
        return UUID.randomUUID().toString();
    }
    
    // ----------------------------------------------------------------------
    // add
    
    @Override
    public void addCalendarLinkageWithId(PartakeConnection con, String calendarId, CalendarLinkage embryo) throws DAOException {
        try {
            CassandraConnection ccon = (CassandraConnection) con;
            addCalendarLinkageWithId(ccon.getClient(), calendarId, embryo, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    private void addCalendarLinkageWithId(Client client, String calendarId, CalendarLinkage embryo, long time) throws Exception {
        String key = CALENDAR_PREFIX + calendarId;
        
        List<Mutation> mutations = new ArrayList<Mutation>(); 
        mutations.add(createMutation("userId", embryo.getUserId(), time));

        client.batch_mutate(CALENDAR_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(CALENDAR_COLUMNFAMILY, mutations)), CALENDAR_CL_W);        
    }
    
    // ----------------------------------------------------------------------
    // get

    @Override
    public CalendarLinkage getCalendarLinkageById(PartakeConnection con, String calendarId) throws DAOException {
        try {
            CassandraConnection ccon = (CassandraConnection) con;
            return getCalendarLinkageById(ccon.getClient(), calendarId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    };
    
    private CalendarLinkage getCalendarLinkageById(Client client, String calendarId, long time) throws Exception {
        String key = CALENDAR_PREFIX + calendarId;

        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 100);
        predicate.setSlice_range(sliceRange);

        ColumnParent parent = new ColumnParent(CALENDAR_COLUMNFAMILY);
        List<ColumnOrSuperColumn> results = client.get_slice(CALENDAR_KEYSPACE, key, parent, predicate, CALENDAR_CL_R);

        if (results == null || results.isEmpty()) { return null; }
        
        CalendarLinkage calendarLinkage = new CalendarLinkage();
        calendarLinkage.setId(calendarId);
        
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            String name = string(column.getName());
            String value = string(column.getValue());
            
            if ("userId".equals(name)) {
                calendarLinkage.setUserId(value);
            }
        }            

        return calendarLinkage.freeze();
    }
    
    // ----------------------------------------------------------------------
    // remove
    
    @Override
    public void removeCalendarLinkageById(PartakeConnection con, String calendarId) throws DAOException {
        try {
            CassandraConnection ccon = (CassandraConnection) con;
            removeCalendarLinkageById(ccon.getClient(), calendarId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    private void removeCalendarLinkageById(Client client, String calendarId, long time) throws Exception {
        String key = CALENDAR_PREFIX + calendarId;
        ColumnPath columnPath = new ColumnPath(CALENDAR_COLUMNFAMILY);
        client.remove(CALENDAR_KEYSPACE, key, columnPath, time, CALENDAR_CL_W);
    }
    
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        
    }

}
