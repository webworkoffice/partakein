package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.pk.EnrollmentPK;
import in.partake.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.thrift.Cassandra.Client;


// * participation
//		users:events:<event id>
//  		<user id>: { modifiedAt: <modifiedAt>, status: <participation status>, comment: <string> }
//
// * from user id (events the user will participate in)
//		events:user:<user id>
//			<event id>/<participation status>
//		まあここはあまり見られない情報なのでちょっとぐらい遅くても良い様な気がするのでこれで。
//
class EnrollmentCassandraDao extends CassandraDao implements IEnrollmentAccess {    
    // USER PARTICIPATION TABLE
    private static final String USERS_ENROLLMENT_PREFIX = "users:events:";
    private static final String USERS_ENROLLMENT_KEYSPACE = "Keyspace1";
    private static final String USERS_ENROLLMENT_COLUMNFAMILY = "Super1";
    private static final ConsistencyLevel USERS_ENROLLMENT_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel USERS_ENROLLMENT_CL_W = ConsistencyLevel.ALL;

    // Events By Participant
    private static final String EVENTS_ENROLLMENT_PREFIX = "events:user:";
    private static final String EVENTS_ENROLLMENT_KEYSPACE = "Keyspace1";
    private static final String EVENTS_ENROLLMENT_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel EVENTS_ENROLLMENT_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel EVENTS_ENROLLMENT_CL_W = ConsistencyLevel.ALL;

    EnrollmentCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    @Override
    public void put(PartakeConnection con, Enrollment enrollment) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            putImplForEvent(ccon.getClient(), enrollment.getUserId(), enrollment.getEventId(), enrollment.getStatus(), ccon.getAcquiredTime());
            putImpl(ccon.getClient(), enrollment, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public Enrollment find(PartakeConnection con, EnrollmentPK pk) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return findImpl(ccon, pk.getUserId(), pk.getEventId());
        } catch (Exception e) {
            throw new DAOException(e);
        }
        
    }
    
    @Override
    public void remove(PartakeConnection con, EnrollmentPK pk) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            removeImpl(ccon.getClient(), pk.getUserId(), pk.getEventId(), ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }        
    }

    @Override
    public DataIterator<Enrollment> getIterator(PartakeConnection con) throws DAOException {
        return new CassandraKeyColumnDataIterator<Enrollment>((CassandraConnection) con, 
                new CassandraTableDescription(USERS_ENROLLMENT_PREFIX, USERS_ENROLLMENT_KEYSPACE, USERS_ENROLLMENT_COLUMNFAMILY, USERS_ENROLLMENT_CL_R, USERS_ENROLLMENT_CL_W),
                new EnrollmentMapper((CassandraConnection) con, factory));
                
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        removeAllData((CassandraConnection) con);
    }

    @Override
    public List<Enrollment> findByEventId(PartakeConnection con, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return findByEventIdImpl(ccon, eventId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public List<Enrollment> findByUserId(PartakeConnection con, String userId) throws DAOException {
        try {
            return findByUserIdImpl((CassandraConnection) con, userId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    
    // ----------------------------------------------------------------------

    private void putImpl(Client client, Enrollment enrollment, long time) throws Exception {
        String key = USERS_ENROLLMENT_PREFIX + enrollment.getEventId();

        List<Column> columns = new ArrayList<Column>();
        columns.add(createColumn("status", enrollment.getStatus().toString(), time));
        columns.add(createColumn("lastStatus", enrollment.getModificationStatus().toString(), time));
        columns.add(createColumn("attendance", enrollment.getAttendanceStatus().toString(), time));
        columns.add(createColumn("comment", enrollment.getComment(), time));
        columns.add(createColumn("vip", String.valueOf(enrollment.isVIP()), time));
        columns.add(createColumn("modifiedAt", enrollment.getModifiedAt(), time));
        columns.add(createColumn("deleted", "false", time));
        
        SuperColumn superColumn = new SuperColumn(bytes(enrollment.getUserId()), columns);
        
        List<Mutation> mutations = new ArrayList<Mutation>();
        mutations.add(createSuperColumnMutation(superColumn));
        
        client.batch_mutate(USERS_ENROLLMENT_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(USERS_ENROLLMENT_COLUMNFAMILY, mutations)), USERS_ENROLLMENT_CL_W);
    }    
        
    /**
     * ユーザーから event 参加リストをひけるテーブルに、参加を追加する。
     * @param client
     * @param user
     * @param event
     * @param status
     * @param time
     * @throws Exception
     */
	private void putImplForEvent(Client client, String userId, String eventId, ParticipationStatus status, long time) throws Exception {
		String key = EVENTS_ENROLLMENT_PREFIX + userId;
		
    	ColumnPath columnPath = new ColumnPath(EVENTS_ENROLLMENT_COLUMNFAMILY);
    	columnPath.setColumn(bytes(eventId));
    	client.insert(EVENTS_ENROLLMENT_KEYSPACE, key, columnPath, bytes(status.toString()), time, EVENTS_ENROLLMENT_CL_W);
	}
	
    private Enrollment findImpl(CassandraConnection con, String userId, String eventId) throws Exception {
        Client client = con.getClient();
        String key = USERS_ENROLLMENT_PREFIX + eventId;
        ColumnPath cp = new ColumnPath(USERS_ENROLLMENT_COLUMNFAMILY);
        cp.setSuper_column(bytes(userId));
        
        try {
            ColumnOrSuperColumn cosc = client.get(USERS_ENROLLMENT_KEYSPACE, key, cp, USERS_ENROLLMENT_CL_R);
            Enrollment enrollment = convertToEnrollment(con, eventId, cosc);
            if (enrollment != null) { return enrollment.freeze(); }
            else { return null; }
        } catch (NotFoundException e) {
            return null;
        }
    }
    
    private void removeImpl(Client client, String userId, String eventId, long time) throws Exception {
        String key = USERS_ENROLLMENT_PREFIX + eventId;

        List<Column> columns = new ArrayList<Column>();
        columns.add(createColumn("deleted", "true", time));
        
        SuperColumn superColumn = new SuperColumn(bytes(userId), columns);
        
        List<Mutation> mutations = new ArrayList<Mutation>();
        mutations.add(createSuperColumnMutation(superColumn));
        
        client.batch_mutate(USERS_ENROLLMENT_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(USERS_ENROLLMENT_COLUMNFAMILY, mutations)), USERS_ENROLLMENT_CL_W);
 
    }
    

	// 順序通りにならんでないことに注意。
    private List<Enrollment> findByEventIdImpl(CassandraConnection con, String eventId) throws Exception {
        Client client = con.getClient();
        String key = USERS_ENROLLMENT_PREFIX + eventId;

        SlicePredicate predicate = new SlicePredicate();
        // TODO: これは iterator を返すべき。
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 1000); 
        predicate.setSlice_range(sliceRange);

        ColumnParent columnParent = new ColumnParent(USERS_ENROLLMENT_COLUMNFAMILY);

        List<ColumnOrSuperColumn> results = client.get_slice(USERS_ENROLLMENT_KEYSPACE, key, columnParent, predicate, USERS_ENROLLMENT_CL_R);
        
        ArrayList<Enrollment> participations = new ArrayList<Enrollment>();
        
        for (ColumnOrSuperColumn cosc : results) {
            SuperColumn superColumn = cosc.getSuper_column();
            if (superColumn == null) { continue; }
            Enrollment enrollment = convertToEnrollment(con, eventId, cosc);
            if (enrollment != null) {
                participations.add(enrollment);
            }
        }

        return participations;
    }    
    
    private List<Enrollment> findByUserIdImpl(CassandraConnection con, String userId) throws Exception {
        String key = EVENTS_ENROLLMENT_PREFIX + userId;

        ColumnIterator it = new ColumnIterator(con, 
                EVENTS_ENROLLMENT_KEYSPACE, key, EVENTS_ENROLLMENT_COLUMNFAMILY, false, EVENTS_ENROLLMENT_CL_R, EVENTS_ENROLLMENT_CL_W);
        
        List<Enrollment> enrollments = new ArrayList<Enrollment>();
        while (it.hasNext()) {
            ColumnOrSuperColumn cosc = it.next();
            Column column = cosc.getColumn();
            if (column == null) { continue; }
            String eventId = string(column.getName());
            
            Enrollment enrollment = find(con, new EnrollmentPK(userId, eventId)); 
            enrollments.add(enrollment);
        }
        
        return enrollments;
    }
    
    private Enrollment convertToEnrollment(CassandraConnection con, String eventId, ColumnOrSuperColumn cosc) throws DAOException {
        return new EnrollmentMapper(con, factory).map(cosc, eventId);
    }
}

class EnrollmentMapper extends ColumnOrSuperColumnKeyMapper<Enrollment> {

    public EnrollmentMapper(CassandraConnection connection, PartakeDAOFactory factory) {
        super(connection, factory);
    }

    @Override
    public Enrollment map(ColumnOrSuperColumn cosc, String key) throws DAOException {
        String eventId = key;
        SuperColumn superColumn = cosc.getSuper_column();
        if (superColumn == null) { return null; }
        
        String userId = string(cosc.getSuper_column().getName());
        String comment = null;
        ParticipationStatus status = null;
        ModificationStatus lastStatus = ModificationStatus.CHANGED;
        AttendanceStatus attendanceStatus = AttendanceStatus.UNKNOWN;
        Date modifiedAt = null;
        Date modifiedAt2 = null;
        boolean vip = false;
        
        // TODO: 歴史的負の遺産が多すぎるのであとで直す。
        for (Column column : superColumn.getColumns()) {
            String name = string(column.getName());
            if ("status".equals(name)) {
                // user = userDao.find(connection, string(superColumn.getName()));
                status = ParticipationStatus.safeValueOf(string(column.getValue()));
                modifiedAt2 = new Date(column.timestamp);
            } else if ("lastStatus".equals(name)) {
                lastStatus = ModificationStatus.safeValueOf(string(column.getValue()));
            } else if ("comment".equals(name)) {
                comment = string(column.getValue());
            } else if ("vip".equals(name)) { 
                if ("true".equals(string(column.getValue()))) {
                    vip = true;
                }
            } else if ("modifiedAt".equals(string(column.getName()))) {
                modifiedAt = Util.dateFromTimeString(string(column.getValue()));
            } else if ("attendance".equals(string(column.getName()))) {
                attendanceStatus = AttendanceStatus.safeValueOf(string(column.getValue()));
            } else if ("deleted".equals(name)) {
                if ("true".equals(string(column.getValue()))) {
                    return null;
                }
            }
        }
                    
        if (modifiedAt != null) {
            return new Enrollment(userId, eventId, comment, status, vip, lastStatus, attendanceStatus, modifiedAt);
        } else {
            return new Enrollment(userId, eventId, comment, status, vip, lastStatus, attendanceStatus, modifiedAt2);
        }        
    }

    @Override
    public ColumnOrSuperColumn unmap(Enrollment t, long time) throws DAOException {
        throw new UnsupportedOperationException();
    }
    
}
