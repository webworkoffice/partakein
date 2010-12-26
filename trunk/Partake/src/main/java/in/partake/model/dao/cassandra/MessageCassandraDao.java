package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IMessageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventNotificationStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;

import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SuperColumn;


/**
 * TABLE 構造
 * <pre>
 *  DEPRECATED
 *	messages:<message id>
 *      event/<event id>
 *      sender/<user id>
 *      message/<string>
 *      deleted/<true or false>
 *	
 *		メッセージの基本構造。sender は基本的に event owner がつとめる。
 *      TODO: event は必要なのかなー？
 *
 *  DEPRECATED
 *	messages:event:<event id>
 *		<time>/<message id>		
 *
 *		ある event に関して、管理者が送ったメッセージを格納する。主に制約条件のチェックに使われる。 
 *		TODO: time は vector clock 的なものを使いたいな...ここは timeBasedUUID にしたほうがいいかもしれない。
 *		TODO: time は time + message id にしないとかぶる可能性がある。
 *
 *  
 * 	messages:reminder
 *		<event id>/{ "beforeDeadline"/<true false>, "beforeDeadlineHalfday"/<true false>, "beforeTheDay"/<true false> }
 *
 *		ある event に対して、reminder を送ったかどうかを表す。
 *		おくれる notification は、次の３つ。一度送った notification は再び送られないので注意する。(特に締め切りを変更する場合に注意する)
 *			1. 締め切り１日前 	  (beforeDeadline)
 *			2. 締め切り１２時間前 (beforeDeadlineHalfday)	
 *			3. 参加前日		  (beforeTheDay)
 *		送った notification に関しては、true を設定する。
 *		全ての reminder が true になると、消去される。
 *
 *
 *  DEPRECATED
 *	messages:queue
 *		<time>/{ messageId: <id>, status:<queued, sending> }
 *
 *		ある message が、queuing 中か sending 中かを表す。　
 *		TODO: ここの time は厳密に time というより message を作成した時刻で並べば OK
 *		TODO: この time も time + message id にしなければならない。
 *		notification もここに queued で入れておけば可能なはず。
 *
 *  DEPRECATED
 *	messages:sending:<message id>
 *		<user>/numTried
 *		ある message が、user に何回 send が try されたかを表す。
 *
 *
 *  
 *
 *
 * </pre>
 * 
 * messages はどっかにためておいて、messageservice が cassandra から取ってきては送る、送ったら消す、というようにする。
 * event に入れたあとマスターに入れる。次に queueing する。queueing できなければ error を返す。
 * message を送るのは 1thread のみ。それ以上使いたい場合は振り分けしてやる機構を導入する必要がある。
 * 
 * これ以降は実装されていない
 * notification info
		ユーザー・イベントごとに、受け取る・受け取らないを設定できる必要がある
		できたら、設定が可能になるとよい。
 */
class MessageCassandraDao extends CassandraDao implements IMessageAccess {

//	// MESSAGE MASTER TABLE
//    private static final String MESSAGES_PREFIX = "messages:id:";
//    private static final String MESSAGES_KEYSPACE = "Keyspace1";
//    private static final String MESSAGES_COLUMNFAMILY = "Standard2";
//    private static final ConsistencyLevel MESSAGES_CL_R = ConsistencyLevel.ONE;
//    private static final ConsistencyLevel MESSAGES_CL_W = ConsistencyLevel.ALL;
//	
//    private static final String MESSAGES_EVENT_PREFIX = "messages:event:";
//    private static final String MESSAGES_EVENT_KEYSPACE = "Keyspace1";
//    private static final String MESSAGES_EVENT_COLUMNFAMILY = "Standard2";
//    private static final ConsistencyLevel MESSAGES_EVENT_CL_R = ConsistencyLevel.ONE;
//    private static final ConsistencyLevel MESSAGES_EVENT_CL_W = ConsistencyLevel.ALL;
//    
//    // waiting queue とか名前をつけたい
//    private static final String MESSAGES_QUEUE_PREFIX = "messages:queue";
//    private static final String MESSAGES_QUEUE_KEYSPACE = "Keyspace1";
//    private static final String MESSAGES_QUEUE_COLUMNFAMILY = "Super1";
//    private static final ConsistencyLevel MESSAGES_QUEUE_CL_R = ConsistencyLevel.ONE;
//    private static final ConsistencyLevel MESSAGES_QUEUE_CL_W = ConsistencyLevel.ALL;
//
//    private static final String MESSAGES_SENDING_PREFIX = "messages:sending";
//    private static final String MESSAGES_SENDING_KEYSPACE = "Keyspace1";
//    private static final String MESSAGES_SENDING_COLUMNFAMILY = "Standard2";
//    private static final ConsistencyLevel MESSAGES_SENDING_CL_R = ConsistencyLevel.ONE;
//    private static final ConsistencyLevel MESSAGES_SENDING_CL_W = ConsistencyLevel.ALL;
    
    private static final String MESSAGES_REMINDER_PREFIX = "messages:reminder";
    private static final String MESSAGES_REMINDER_KEYSPACE = "Keyspace1";
    private static final String MESSAGES_REMINDER_COLUMNFAMILY = "Super1";
    private static final ConsistencyLevel MESSAGES_REMINDER_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel MESSAGES_REMINDER_CL_W = ConsistencyLevel.ALL;
    
    // -----
    private static final String MESSAGES_REMINDER_KEY_BEFORE_DEADLINE_ONEDAY  = "beforeDeadline";
    private static final String MESSAGES_REMINDER_KEY_BEFORE_DEADLINE_HALFDAY = "beforeDeadlineHalfday";
    private static final String MESSAGES_REMINDER_KEY_BEFORE_THEDAY           = "beforeTheDay";

    
    MessageCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
	/* (non-Javadoc)
     * @see in.partake.dao.cassandra.IMessageAccess#addNotification(java.lang.String)
     */
	@Override
	public void addNotification(PartakeConnection con, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addNotification(ccon.getClient(), eventId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
	}
	
	/* (non-Javadoc)
     * @see in.partake.dao.cassandra.IMessageAccess#getNotificationStatus(java.lang.String)
     */
	@Override
	public EventNotificationStatus getNotificationStatus(PartakeConnection con, String eventId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            EventNotificationStatus eventNotificationStatus = getNotificationStatus(ccon.getClient(), eventId);
            if (eventNotificationStatus == null) {
                return new EventNotificationStatus(eventId, true, true, true);
            } else {
                return eventNotificationStatus;
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
	}
	
	@Override
	public DataIterator<EventNotificationStatus> getNotificationStatuses(PartakeConnection connection) throws DAOException {
	    try {
	        return getNotificationStatusesImpl((CassandraConnection) connection);
	    } catch (Exception e) {
	        throw new DAOException(e);
	    }
	}
	
	// ----------------------------------------------------------------------

	private void addNotification(Client client, String eventId, long time) throws Exception {
		String key = MESSAGES_REMINDER_PREFIX;
		
        Map<String, List<ColumnOrSuperColumn>> cfmap = new HashMap<String, List<ColumnOrSuperColumn>>();
        List<ColumnOrSuperColumn> columns = new ArrayList<ColumnOrSuperColumn>();
                
        {
	        SuperColumn superColumn = new SuperColumn();
	        superColumn.setName(bytes(eventId));
	        
	        superColumn.addToColumns(new Column(bytes(MESSAGES_REMINDER_KEY_BEFORE_DEADLINE_ONEDAY), bytes("false"), time));
	        superColumn.addToColumns(new Column(bytes(MESSAGES_REMINDER_KEY_BEFORE_DEADLINE_HALFDAY), bytes("false"), time));
	        superColumn.addToColumns(new Column(bytes(MESSAGES_REMINDER_KEY_BEFORE_THEDAY), bytes("false"), time));
	        
	        columns.add(new ColumnOrSuperColumn().setSuper_column(superColumn));
        }
        
        cfmap.put(MESSAGES_REMINDER_COLUMNFAMILY, columns);
        
        client.batch_insert(MESSAGES_REMINDER_KEYSPACE, key, cfmap, MESSAGES_REMINDER_CL_W);
	}
	
	private EventNotificationStatus getNotificationStatus(Client client, String eventId) throws Exception {
		String key = MESSAGES_REMINDER_PREFIX;
		
		try {
			ColumnPath columnPath = new ColumnPath(MESSAGES_REMINDER_COLUMNFAMILY);
			columnPath.setSuper_column(bytes(eventId));
			
			ColumnOrSuperColumn cosc = client.get(MESSAGES_REMINDER_KEYSPACE, key, columnPath, MESSAGES_REMINDER_CL_R);
			if (cosc == null) { return null; }
			
			SuperColumn superColumn = cosc.getSuper_column();
			if (superColumn == null) { return null; }
			
			EventNotificationStatus status = new EventNotificationStatus();
			status.setEventId(eventId);
			
			for (Column column : superColumn.columns) {
				String k = string(column.getName());
				String v = string(column.getValue());
			
				if (MESSAGES_REMINDER_KEY_BEFORE_DEADLINE_ONEDAY.equals(k) && "true".equals(v)) {
				    status.setBeforeDeadlineOneday(true);
				} else if (MESSAGES_REMINDER_KEY_BEFORE_DEADLINE_HALFDAY.equals(k) && "true".equals(v)) {
					status.setBeforeDeadlineHalfday(true);
				} else if (MESSAGES_REMINDER_KEY_BEFORE_THEDAY.equals(k) && "true".equals(v)) {
					status.setBeforeTheDay(true);
				}
			}
			
			return status;
		} catch (NotFoundException e) {
			return null;
		}
		
	}
	
	private DataIterator<EventNotificationStatus> getNotificationStatusesImpl(CassandraConnection connection) throws Exception {
		String key = MESSAGES_REMINDER_PREFIX;

		ColumnIterator iterator = new ColumnIterator(connection, factory, MESSAGES_REMINDER_KEYSPACE, key, MESSAGES_REMINDER_COLUMNFAMILY, false, MESSAGES_REMINDER_CL_R, MESSAGES_REMINDER_CL_W);
		
		return new CassandraDataIterator<EventNotificationStatus>(iterator, new ColumnOrSuperColumnMapper<EventNotificationStatus>(connection, factory) {
		    @Override
		    public EventNotificationStatus map(ColumnOrSuperColumn cosc) throws DAOException {
                // TODO: なんでこれ getNotificationStatus() を読んでないの？
                // 明らかに共通化できるのでするべき。
                SuperColumn superColumn = cosc.getSuper_column();
                EventNotificationStatus status = new EventNotificationStatus();
                for (Column column : superColumn.columns) {
                    String key = string(column.getName());
                    String value = string(column.getValue());
                    
                    if (MESSAGES_REMINDER_KEY_BEFORE_DEADLINE_ONEDAY.equals(key) && "true".equals(value)) {
                        status.setBeforeDeadlineOneday(true);
                    } else if (MESSAGES_REMINDER_KEY_BEFORE_DEADLINE_HALFDAY.equals(key) && "true".equals(value)) {
                        status.setBeforeDeadlineHalfday(true);
                    } else if (MESSAGES_REMINDER_KEY_BEFORE_THEDAY.equals(key) && "true".equals(value)) {
                        status.setBeforeTheDay(true);
                    }
                }
                
                String eventId = string(superColumn.getName());
                
                status.setEventId(eventId);
                return status;
		    }
		    
		    @Override
		    public ColumnOrSuperColumn unmap(EventNotificationStatus t) throws DAOException {
                SuperColumn superColumn = new SuperColumn();

                long time = new Date().getTime();
                byte[] TRUE = bytes("true");
                byte[] FALSE = bytes("false");
                
                superColumn.setName(bytes(t.getEventId()));
                superColumn.addToColumns(new Column(bytes(MESSAGES_REMINDER_KEY_BEFORE_DEADLINE_ONEDAY),  t.isBeforeDeadlineOneday() ? TRUE : FALSE, time));
                superColumn.addToColumns(new Column(bytes(MESSAGES_REMINDER_KEY_BEFORE_DEADLINE_HALFDAY), t.isBeforeDeadlineHalfday() ? TRUE : FALSE, time));
                superColumn.addToColumns(new Column(bytes(MESSAGES_REMINDER_KEY_BEFORE_THEDAY),           t.isBeforeTheDay() ? TRUE : FALSE, time));
                
                ColumnOrSuperColumn cosc = new ColumnOrSuperColumn();
                cosc.setSuper_column(superColumn);
                return cosc;
		    }
		});
	}

}


