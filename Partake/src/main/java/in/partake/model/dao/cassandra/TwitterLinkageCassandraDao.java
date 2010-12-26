package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.ITwitterLinkageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.TwitterLinkage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;



class TwitterLinkageCassandraDao extends CassandraDao implements ITwitterLinkageAccess {
	
	// USER MASTER TABLE
    private static final String TWITTER_PREFIX = "twitter:id:";
    private static final String TWITTER_KEYSPACE = "Keyspace1";
    private static final String TWITTER_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel TWITTER_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel TWITTER_CL_W = ConsistencyLevel.ALL;

    TwitterLinkageCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }

	@Override
	public int addTwitterLinkage(PartakeConnection con, TwitterLinkage embryo) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return addTwitterLinkage(ccon.getClient(), embryo, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
	}

    @Override
    public TwitterLinkage getTwitterLinkageById(PartakeConnection con, int twitterId) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getTwitterLinkageById(ccon.getClient(), twitterId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
	
	// ----------------------------------------------------------------------
	
	private int addTwitterLinkage(Client client, TwitterLinkage embryo, long time) throws Exception {
		int id = embryo.getTwitterId();
    	String key = TWITTER_PREFIX + id;

        Map<String, List<ColumnOrSuperColumn>> cfmap = new HashMap<String, List<ColumnOrSuperColumn>>();
        List<ColumnOrSuperColumn> columns = new ArrayList<ColumnOrSuperColumn>();

        columns.add(new ColumnOrSuperColumn().setColumn(new Column(bytes("screenName"), bytes(embryo.getScreenName()), time)));
        columns.add(new ColumnOrSuperColumn().setColumn(new Column(bytes("name"), bytes(embryo.getName()), time)));        
        columns.add(new ColumnOrSuperColumn().setColumn(new Column(bytes("accessToken"), bytes(embryo.getAccessToken()), time)));
        columns.add(new ColumnOrSuperColumn().setColumn(new Column(bytes("accessTokenSecret"), bytes(embryo.getAccessTokenSecret()), time)));
        columns.add(new ColumnOrSuperColumn().setColumn(new Column(bytes("profileImageURL"), bytes(embryo.getProfileImageURL()), time)));
        columns.add(new ColumnOrSuperColumn().setColumn(new Column(bytes("userId"), bytes(embryo.getUserId()), time)));
        
        cfmap.put(TWITTER_COLUMNFAMILY, columns);

        client.batch_insert(TWITTER_KEYSPACE, key, cfmap, TWITTER_CL_W);
        
        return id;
	}

	
	private TwitterLinkage getTwitterLinkageById(Client client, int twitterId) throws Exception {
		String key = TWITTER_PREFIX + twitterId;
    	
        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 100);
        predicate.setSlice_range(sliceRange);

        ColumnParent parent = new ColumnParent(TWITTER_COLUMNFAMILY);

        List<ColumnOrSuperColumn> results = client.get_slice(TWITTER_KEYSPACE, key, parent, predicate, TWITTER_CL_R);

        TwitterLinkage linkage = new TwitterLinkage();
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            String name = string(column.getName());
            String value = string(column.getValue());
            
            if ("screenName".equals(name)) {
                linkage.setScreenName(value);
            } else if ("name".equals(name)) {
                linkage.setName(value);
            } else if ("accessToken".equals(name)) {
                linkage.setAccessToken(value);
            } else if ("accessTokenSecret".equals(name)) {
                linkage.setAccessTokenSecret(value);
            } else if ("profileImageURL".equals(name)) {
                linkage.setProfileImageURL(value);
            } else if ("userId".equals(name)) {
                linkage.setUserId(value);
            }
        }
        
        return linkage.freeze();
	}
}
