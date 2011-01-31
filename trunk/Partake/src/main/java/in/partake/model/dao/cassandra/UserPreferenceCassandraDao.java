package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IUserPreferenceAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.Cassandra.Client;


class UserPreferenceCassandraDao extends CassandraDao implements IUserPreferenceAccess {

	// PREFERENCE MASTER TABLE
    private static final String PREFERENCES_PREFIX = "preference:id:";
    private static final String PREFERENCES_KEYSPACE = "Keyspace1";
    private static final String PREFERENCES_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel PREFERENCES_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel PREFERENCES_CL_W = ConsistencyLevel.ALL;
	

    UserPreferenceCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
	@Override
	public UserPreference getPreference(PartakeConnection con, String userId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getPreference(ccon.getClient(), userId);            
        } catch (Exception e) {
            throw new DAOException(e);
        }
	}
	
	@Override
	public void setPreference(PartakeConnection con, UserPreference embryo) throws DAOException {
	    if (embryo == null) { throw new IllegalArgumentException("embryo should not be null"); }
	    if (embryo.getUserId() == null) { throw new IllegalArgumentException("userId should not be null."); }
	    
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            setPreferenceImpl(ccon.getClient(), embryo, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
	}
	
	@Override
	public void truncate(PartakeConnection con) throws DAOException {
	    removeAllData((CassandraConnection) con);
	}
	
	// ----------------------------------------------------------------------
	
	private UserPreference getPreference(Client client, String userId) throws Exception {
		String key = PREFERENCES_PREFIX + userId;
    	
        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 100);
        predicate.setSlice_range(sliceRange);

        ColumnParent parent = new ColumnParent(PREFERENCES_COLUMNFAMILY);

        List<ColumnOrSuperColumn> results = client.get_slice(PREFERENCES_KEYSPACE, key, parent, predicate, PREFERENCES_CL_R);

        if (results.isEmpty()) { return null; }

        boolean profilePublic = true;
        boolean receivingTwitterMessage = true;
        boolean tweetingAttendanceAutomatically = false;
        
        for (ColumnOrSuperColumn result : results) {
        	Column column = result.column;
            String name = string(column.getName());
            String value = string(column.getValue());
            
            if ("profilePublic".equals(name)) {
            	profilePublic = "true".equals(value);
            } else if ("receivingTwitterMessage".equals(name)) {
            	receivingTwitterMessage = "true".equals(value);
            } else if ("tweetingAttendanceAutomatically".equals(name)) {
            	tweetingAttendanceAutomatically = "true".equals(value);
            }
        }
        
        UserPreference impl = new UserPreference(userId, profilePublic, receivingTwitterMessage, tweetingAttendanceAutomatically);
        return impl.freeze();
	}
	
	private void setPreferenceImpl(Client client, UserPreference embryo, long time) throws Exception {
		String key = PREFERENCES_PREFIX + embryo.getUserId();
		
        Map<String, List<ColumnOrSuperColumn>> cfmap = new HashMap<String, List<ColumnOrSuperColumn>>();
        List<ColumnOrSuperColumn> columns = new ArrayList<ColumnOrSuperColumn>();

        columns.add(new ColumnOrSuperColumn().setColumn(new Column(bytes("profilePublic"), embryo.isProfilePublic() ? TRUE : FALSE, time)));
        columns.add(new ColumnOrSuperColumn().setColumn(new Column(bytes("receivingTwitterMessage"), embryo.isReceivingTwitterMessage() ? TRUE : FALSE, time)));
        columns.add(new ColumnOrSuperColumn().setColumn(new Column(bytes("tweetingAttendanceAutomatically"), embryo.tweetsAttendanceAutomatically() ? TRUE : FALSE, time)));        
        
        cfmap.put(PREFERENCES_COLUMNFAMILY, columns);
        
        client.batch_insert(PREFERENCES_KEYSPACE, key, cfmap, PREFERENCES_CL_W);
	}
}
