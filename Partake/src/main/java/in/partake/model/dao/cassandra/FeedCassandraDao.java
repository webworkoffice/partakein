package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IFeedAccess;
import in.partake.model.dao.PartakeConnection;

/**
 * Since some events may be private, the feed id should not be able to be guessed from the event id.
 * So the feed id should be different from the event id. 
 */
class FeedCassandraDao extends CassandraDao implements IFeedAccess {
    
    
    
    @Override
    public void addFeedId(PartakeConnection con, String feedId, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet");
    }
    
    @Override
    public String getEventIdByFeedId(PartakeConnection con, String feedId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet");
    }
    
    @Override
    public String getFeedIdByEventId(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub        
        throw new RuntimeException("Not implemented yet");
    }
}
//class FeedDao extends CassandraDao {
//    //
//    private static final String FEED_PREFIX = "feeds:id:";
//    private static final String FEED_KEYSPACE = "Keyspace1";
//    private static final String FEED_COLUMNFAMILY = "Standard2";
//    private static final ConsistencyLevel FEED_CL_R = ConsistencyLevel.ONE;
//    private static final ConsistencyLevel FEED_CL_W = ConsistencyLevel.ALL;
//
//    FeedDao() {
//    	// Do nothing for now.
//    }
//    
//    /**
//     * get fresh id for binary.
//     * @return
//     */
//    public String getFreshId() throws DAOException {
//    	return UUID.randomUUID().toString();
//    }
// 
//    /**
//     * get the corresponding event id from the specified feed id.
//     * @param feedId
//     * @return
//     */
//    public String getEventIdByFeedId(String feedId) throws DAOException {
//    	CassandraClient client = getClient();
//    	try {
//    	    return getEventIdByFeedId(client.getCassandra(), feedId);
//    	} catch (Exception e) {
//    		throw new DAOException(e);
//    	} finally {
//        	invalidate(client);
//        }
//    }
//    
//    /**
//     * create a new feed id from the event id.
//     * @param eventId
//     * @return
//     */
//    public String addFeedWithEventId(String feedId, String eventId) throws DAOException {
//    	CassandraClient client = getClient();
//    	try {
//    		long time = new Date().getTime();
//    	    return addFeedWithEventId(client.getCassandra(), feedId, eventId, time);
//    	} catch (Exception e) {
//    		throw new DAOException(e);
//    	} finally {
//        	invalidate(client);
//        }    	
//    }
//    
//    // ----------------------------------------------------------------------
//    
//    private String getEventIdByFeedId(Client client, String feedId) throws Exception {
//        String key = FEED_PREFIX + feedId;
//        
//        SlicePredicate predicate = new SlicePredicate();
//        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 100);
//        predicate.setSlice_range(sliceRange);
//
//        ColumnParent parent = new ColumnParent(FEED_COLUMNFAMILY);
//        List<ColumnOrSuperColumn> results = client.get_slice(FEED_KEYSPACE, key, parent, predicate, FEED_CL_R);
//
//        if (results == null || results.isEmpty()) { return null; }
//        
//        BinaryDataImpl data = new BinaryDataImpl();
//        for (ColumnOrSuperColumn result : results) {
//            Column column = result.column;
//            String name = string(column.getName());
//            
//        	if ("eventId".equals(name)) {
//        		return string(column.getValue());
//        	}
//        }
//        
//        return null;
//    }
//    
//    private String addFeedWithEventId(Client client, String feedId, String eventId, long time) throws Exception {
//        String key = FEED_PREFIX + feedId;
//
//        List<Mutation> mutations = new ArrayList<Mutation>(); 
//
//        mutations.add(createColumnMutation("eventId", eventId, time));
//        
//        client.batch_mutate(FEED_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(FEED_COLUMNFAMILY, mutations)), FEED_CL_W);
//
//        return feedId;
//    }
//}
