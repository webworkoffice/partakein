package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEnrollmentAccess;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.LastParticipationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
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
    public void addEnrollment(PartakeConnection con, Enrollment enrollment) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addEventEnrollment(ccon.getClient(), enrollment.getUserId(), enrollment.getEventId(), enrollment.getStatus(), ccon.getAcquiredTime());
            addUserEnrollment(ccon.getClient(), enrollment, ccon.getAcquiredTime());
            // addUserEnrollment(ccon.getClient(), user, event, status, oldStatus, comment, changesOnlyComment, forceChangeModifiedAt, time);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public Enrollment getEnrollment(PartakeConnection con, String userId, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void removeEnrollment(PartakeConnection con, String userId, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet");
    }
    
    @Override
    public List<Enrollment> getEnrollmentsByEventId(PartakeConnection con, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getParticipation(ccon, eventId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public List<Enrollment> getEnrollmentsByUserId(PartakeConnection con, String userId) throws DAOException {
        try {
            return getEnrollmentsByUserIdImpl((CassandraConnection) con, userId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        removeAllData((CassandraConnection) con);
    }
    
    // ----------------------------------------------------------------------

    private void addUserEnrollment(Client client, Enrollment enrollment, long time) throws Exception {
        String key = USERS_ENROLLMENT_PREFIX + enrollment.getEventId();

        List<Column> columns = new ArrayList<Column>();
        columns.add(createColumn("status", enrollment.getStatus().toString(), time));
        columns.add(createColumn("lastStutus", enrollment.getLastStatus().toString(), time));
        columns.add(createColumn("comment", enrollment.getComment(), time));
        columns.add(createColumn("priority", String.valueOf(enrollment.getPriority()), time));
        columns.add(createColumn("modifiedAt", enrollment.getModifiedAt(), time));

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
	private void addEventEnrollment(Client client, String userId, String eventId, ParticipationStatus status, long time) throws Exception {
		String key = EVENTS_ENROLLMENT_PREFIX + userId;
		
    	ColumnPath columnPath = new ColumnPath(EVENTS_ENROLLMENT_COLUMNFAMILY);
    	columnPath.setColumn(bytes(eventId));
    	client.insert(EVENTS_ENROLLMENT_KEYSPACE, key, columnPath, bytes(status.toString()), time, EVENTS_ENROLLMENT_CL_W);
	}

    private List<Enrollment> getParticipation(CassandraConnection con, String eventId) throws Exception {
        Client client = con.getClient();
        String key = USERS_ENROLLMENT_PREFIX + eventId;

        SlicePredicate predicate = new SlicePredicate();
        // TODO: これは iterator を返すべき。
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 1000); 
        predicate.setSlice_range(sliceRange);

        ColumnParent columnParent = new ColumnParent(USERS_ENROLLMENT_COLUMNFAMILY);

        List<ColumnOrSuperColumn> results = client.get_slice(USERS_ENROLLMENT_KEYSPACE, key, columnParent, predicate, USERS_ENROLLMENT_CL_R);
        
        ArrayList<Enrollment> participations = new ArrayList<Enrollment>();
        
        for (ColumnOrSuperColumn result : results) {
            SuperColumn superColumn = result.getSuper_column();
            if (superColumn == null) { continue; }
            
            User user = null;
            String comment = null;
            ParticipationStatus status = null;
            LastParticipationStatus lastStatus = LastParticipationStatus.CHANGED;
            Date modifiedAt = null;
            Date modifiedAt2 = null;
            int priority = 0;
            
            // TODO: 歴史的負の遺産が多すぎるのであとで直す。
            IUserAccess userDao = factory.getUserAccess(); 
            for (Column column : superColumn.getColumns()) {
                String name = string(column.getName());
                if ("status".equals(name)) {
                	user = userDao.getUser(con, string(superColumn.getName()));
                	status = ParticipationStatus.safeValueOf(string(column.getValue()));
                	modifiedAt2 = new Date(column.timestamp);
                } else if ("lastStatus".equals(name)) {
                    lastStatus = LastParticipationStatus.safeValueOf(string(column.getValue()));
                } else if ("comment".equals(name)) {
                    comment = string(column.getValue());
                } else if ("priority".equals(name)) {
                    priority = Integer.parseInt(string(column.getValue()));
                } else if ("modifiedAt".equals(string(column.getName()))) {
                	modifiedAt = Util.dateFromTimeString(string(column.getValue()));
                }
            }
                        
            if (user != null && modifiedAt != null) {
            	participations.add(new Enrollment(user.getId(), eventId, comment, status, priority, lastStatus, modifiedAt));
            } else {
            	participations.add(new Enrollment(user.getId(), eventId, comment, status, priority, lastStatus, modifiedAt2));
            }
        }

        Collections.sort(participations, Enrollment.getPriorityBasedComparator());
        
        return participations;
    }    
    
    private List<Enrollment> getEnrollmentsByUserIdImpl(CassandraConnection con, String userId) throws Exception {
        String key = EVENTS_ENROLLMENT_PREFIX + userId;

        ColumnIterator it = new ColumnIterator(con, factory, 
                EVENTS_ENROLLMENT_KEYSPACE, key, EVENTS_ENROLLMENT_COLUMNFAMILY, false, EVENTS_ENROLLMENT_CL_R, EVENTS_ENROLLMENT_CL_W);
        
        List<Enrollment> enrollments = new ArrayList<Enrollment>();
        while (it.hasNext()) {
            ColumnOrSuperColumn cosc = it.next();
            Column column = cosc.getColumn();
            if (column == null) { continue; }
            String eventId = string(column.getName());
            
            Enrollment enrollment = getEnrollment(con, userId, eventId);
            enrollments.add(enrollment);
        }
        
        return enrollments;
    }
    
    
}
