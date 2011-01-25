package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IEnrollmentAccess;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.model.dto.LastParticipationStatus;
import in.partake.model.dto.Participation;
import in.partake.model.dto.ParticipationStatus;
import in.partake.model.dto.User;
import in.partake.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//  	TODO: app 側で sort を行い、最大 1000 人ぐらいまでしか参加できないようにする
//		これどうしようね。本当は updatedAt で並べたい。(どうしようもないので、app 側で sort することにする)
//      TODO: うーん、name は <user id> じゃなくて <user id + time> であるべきだったなあ。微妙。Cassandra からいけないんだよ。
//
// * from user id (events the user will participate in)
//		events:user:<user id>
//			<event id>/<participation status>
//		まあここはあまり見られない情報なのでちょっとぐらい遅くても良い様な気がするのでこれで。
//		TODO: event が消去されていれば消すべき。
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
    
    // TODO: changesOnlyComment がださいのでなんとかする
    // TODO: DAO が仕事しすぎ -- Service がやるべき仕事が入りすぎ
    @Override
    public void enroll(PartakeConnection con, User user, Event event, ParticipationStatus status, String comment, boolean changesOnlyComment,
                    boolean forceChangeModifiedAt) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            ParticipationStatus oldStatus = getParticipationStatus(ccon, event, user); 
            
            long time = ccon.getAcquiredTime();

            if (changesOnlyComment) {
                // TODO: このエラー処理はひどい。あとで直す。
                // TODO: というか、コメントを変更するだけのインタフェースを作成するべき。
                if (oldStatus == null) {
                    throw new DAOException("OldStatus should not be null.");
                }
                addUserEnrollment(ccon.getClient(), user, event, status, oldStatus, comment, changesOnlyComment, forceChangeModifiedAt, time);
            } else {
                // User と Event を追加する。この最中にもしかしたら落ちるかもしれない。
                addEventEnrollment(ccon.getClient(), user, event, status, time);
                addUserEnrollment(ccon.getClient(), user, event, status, oldStatus, comment, changesOnlyComment, forceChangeModifiedAt, time);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public List<Participation> getParticipation(PartakeConnection con, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getParticipation(ccon, eventId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void setLastStatus(PartakeConnection con, String eventId, Participation p, LastParticipationStatus lastStatus) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
         // TODO: 名前をなんとかする！
            updateLastStatus(ccon.getClient(), p.getUserId(), eventId, lastStatus, ccon.getAcquiredTime());
            
        } catch (Exception e) {
            throw new DAOException(e);
        }   
    }
    
    // TODO: DAO が仕事しすぎ
    @Override
    public int getNumOfParticipants(PartakeConnection con, String eventId, boolean isReservationTimeOver) throws DAOException {
        // PartakeCassandraConnection ccon = (PartakeCassandraConnection) con;
        try {
            List<Participation> participations = getParticipation(con, eventId);
            
            int num = 0;
            for (Participation participation : participations) {
                switch (participation.getStatus()) {
                case ENROLLED:
                    ++num; break;
                case RESERVED:
                    if (!isReservationTimeOver) { ++num; }
                    break;
                default:        /* do nothing */ break;
                }
            }
            
            return num;            
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    // TODO: DAO が仕事しすぎ
    @Override
    public int getOrderOfEnrolledEvent(PartakeConnection con, String eventId, String userId, boolean isReservationTimeOver) throws DAOException {
        // PartakeCassandraConnection ccon = (PartakeCassandraConnection) con;
        try {
            List<Participation> participations = getParticipation(con, eventId);

            int order = 1;
            for (Participation participation : participations) {
                switch (participation.getStatus()) {
                case ENROLLED:
                    if (userId.equals(participation.getUserId())) {
                        return order;
                    } else {
                        ++order;
                        break;
                    }
                case RESERVED:
                    if (userId.equals(participation.getUserId())) {
                        return order;
                    } else {
                        if (!isReservationTimeOver) { ++order; }                    
                        break;
                    }
                default:
                    /* do nothing */
                }
            }
            
            return -1;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    /**
     * event の一覧を返す。sort されずに返ってくるので、必要に応じて sort する必要がある。
     * また、cancel したイベントに関しては null が返るので、適切に continue すること。
     * TODO: 美しくない！　なんとかならないか。sort はしょうがないが。
     * TODO: というか List<Event> 返せよ。
     * 
     * @param user
     * @return
     * @throws Exception
     */
    @Override
    public DataIterator<Event> getEnrolledEvents(PartakeConnection connection, String userId) throws DAOException {
        try {
            return getEnrolledEventsImpl((CassandraConnection) connection, userId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public ParticipationStatus getParticipationStatus(PartakeConnection con, Event event, User user) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getParticipationStatus(ccon.getClient(), event, user);
        } catch (Exception e) {
            throw new DAOException(e);
        }        
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not Implemented Yet.");
    }
    
    // ----------------------------------------------------------------------

    private void addUserEnrollment(Client client, User user, Event event,
    		ParticipationStatus status, ParticipationStatus oldStatus, 
    		String comment, boolean changesOnlyComment, boolean forceChangeModifiedAt, long time) throws Exception {
    	String key = USERS_ENROLLMENT_PREFIX + event.getId();
    	
        Map<String, List<ColumnOrSuperColumn>> cfmap = new HashMap<String, List<ColumnOrSuperColumn>>();
        List<ColumnOrSuperColumn> columns = new ArrayList<ColumnOrSuperColumn>();

        {
            SuperColumn superColumn = new SuperColumn();
            superColumn.setName(bytes(String.valueOf(user.getId())));            
            
            superColumn.addToColumns(new Column(bytes("comment"), bytes(comment), time));
            if (changesOnlyComment) {
                // status, modifiedAt の両方共変更しない
            } else if (ParticipationStatus.RESERVED.equals(oldStatus) && ParticipationStatus.ENROLLED.equals(status) && !forceChangeModifiedAt) {
                // RESERVED -> ENROLLED の場合、modifiedAt のみ変更しない
                superColumn.addToColumns(new Column(bytes("status"), bytes(status.toString()), time));                
                // XXX why lastStatus isn't edited?
                //   --> last Status は PARTAKE が最後にこのステータスだと思っているステータスなので、ここでは変更しない。
                //   --> reminder が送られるときに変更される。
            } else if (ParticipationStatus.ENROLLED.equals(oldStatus) && ParticipationStatus.RESERVED.equals(status) && !forceChangeModifiedAt) {
                // ENROLLED -> RESERVED の場合、modifiedAt のみ変更しない                
                superColumn.addToColumns(new Column(bytes("status"), bytes(status.toString()), time));
                // XXX why lastStatus isn't edited?
            } else {
                // それ以外の場合は両方更新
                superColumn.addToColumns(new Column(bytes("status"), bytes(status.toString()), time));
                superColumn.addToColumns(new Column(bytes("lastStatus"), bytes(LastParticipationStatus.CHANGED.toString()), time));
                superColumn.addToColumns(new Column(bytes("modifiedAt"), bytes(Util.getTimeString(time)), time));
            }
            
            columns.add(new ColumnOrSuperColumn().setSuper_column(superColumn));
        }
        
    	cfmap.put(USERS_ENROLLMENT_COLUMNFAMILY, columns);    	
    	client.batch_insert(USERS_ENROLLMENT_KEYSPACE, key, cfmap, USERS_ENROLLMENT_CL_W);
    }
    
    // TODO: 名前をなんとかする！
    private void updateLastStatus(Client client, String userId, String eventId, LastParticipationStatus lastStatus, long time) throws Exception { 
        String key = USERS_ENROLLMENT_PREFIX + eventId;
        
        List<Mutation> mutations = new ArrayList<Mutation>();
        
        SuperColumn superColumn = getSuperColumn(client, USERS_ENROLLMENT_KEYSPACE, USERS_ENROLLMENT_COLUMNFAMILY, userId, key, USERS_ENROLLMENT_CL_R); 
        if (superColumn == null) { return; }
        
        boolean found = false;
        for (Column column : superColumn.getColumns()) {
            if ("lastStatus".equals(string(column.getName()))) {
                column.setValue(bytes(lastStatus.toString()));
                column.setTimestamp(time);
                found = true;
            }
        }
        // if lastStatus does not found, add it.
        if (!found) {
            superColumn.getColumns().add(new Column(bytes("lastStatus"), bytes(lastStatus.toString()), time));
        }
        
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
	private void addEventEnrollment(Client client, User user, Event event, ParticipationStatus status, long time) throws Exception {
		String key = EVENTS_ENROLLMENT_PREFIX + user.getId();
		
    	ColumnPath columnPath = new ColumnPath(EVENTS_ENROLLMENT_COLUMNFAMILY);
    	columnPath.setColumn(bytes(event.getId()));
    	client.insert(EVENTS_ENROLLMENT_KEYSPACE, key, columnPath, bytes(status.toString()), time, EVENTS_ENROLLMENT_CL_W);
	}

    private List<Participation> getParticipation(CassandraConnection con, String eventId) throws Exception {
        Client client = con.getClient();
        String key = USERS_ENROLLMENT_PREFIX + eventId;

        SlicePredicate predicate = new SlicePredicate();
        // TODO: これは iterator を返すべき。
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 1000); 
        predicate.setSlice_range(sliceRange);

        ColumnParent columnParent = new ColumnParent(USERS_ENROLLMENT_COLUMNFAMILY);

        List<ColumnOrSuperColumn> results = client.get_slice(USERS_ENROLLMENT_KEYSPACE, key, columnParent, predicate, USERS_ENROLLMENT_CL_R);
        
        ArrayList<Participation> participations = new ArrayList<Participation>();
        
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
                	user = userDao.getUserById(con, string(superColumn.getName()));
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
            	participations.add(new Participation(user.getId(), comment, status, priority, lastStatus, modifiedAt));
            } else {
            	participations.add(new Participation(user.getId(), comment, status, priority, lastStatus, modifiedAt2));
            }
        }

        Collections.sort(participations, Participation.getPriorityBasedComparator());
        
        return participations;
    }
    
    private ParticipationStatus getParticipationStatus(Client client, Event event, User user) throws IllegalStateException, Exception {
        String key = USERS_ENROLLMENT_PREFIX + event.getId();
        
        ColumnPath columnPath = new ColumnPath(USERS_ENROLLMENT_COLUMNFAMILY);
        columnPath.setSuper_column(bytes(String.valueOf(user.getId())));
        columnPath.setColumn(bytes("status"));
        
        try {
            ColumnOrSuperColumn cosc = client.get(USERS_ENROLLMENT_KEYSPACE, key, columnPath, USERS_ENROLLMENT_CL_R);
            Column column = cosc.getColumn();
            if (column == null) { return ParticipationStatus.NOT_ENROLLED; }
            
            return ParticipationStatus.valueOf(string(column.getValue()));
        } catch (NotFoundException e) {
        	// column が見つからなかったと言うことは、enroll をしていないということであるので、NOT_ENROLLED を返す。
            return ParticipationStatus.NOT_ENROLLED;
        } catch (IllegalArgumentException e) {
        	// TODO: should be logged.
        	e.printStackTrace();
        	return ParticipationStatus.NOT_ENROLLED;
        }
    }
    
    /**
     * get enrolled events from the user.
     * @param client
     * @param user
     * @return
     * @throws Exception
     */
    private CassandraDataIterator<Event> getEnrolledEventsImpl(CassandraConnection connection, String userId) throws Exception {
    	String key = EVENTS_ENROLLMENT_PREFIX + userId;

        ColumnIterator iterator = new ColumnIterator(connection, factory, 
        		EVENTS_ENROLLMENT_KEYSPACE, key, EVENTS_ENROLLMENT_COLUMNFAMILY, false, EVENTS_ENROLLMENT_CL_R, EVENTS_ENROLLMENT_CL_W);
        
        return new CassandraDataIterator<Event>(iterator, new ColumnOrSuperColumnMapper<Event>(connection, factory) {
        	@Override
        	public Event map(ColumnOrSuperColumn cosc) throws DAOException {
        		Column column = cosc.column;
        		if (column == null) { return null; }
        		
        		String eventId = string(column.getName());
        		ParticipationStatus status = ParticipationStatus.valueOf(string(column.getValue()));
        		if (status.isEnrolled()) {
        		    return factory.getEventAccess().getEventById(connection, eventId);
        		} else {
        			return null;
        		}
        	}
        	
        	@Override
        	public ColumnOrSuperColumn unmap(Event event) throws DAOException {
        		throw new UnsupportedOperationException();
        	};
		});
    }
}
