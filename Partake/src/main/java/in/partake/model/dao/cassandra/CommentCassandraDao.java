package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

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

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.ICommentAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Comment;
import in.partake.util.Util;

class CommentCassandraDao extends CassandraDao implements ICommentAccess {
    // 
    private static final String COMMENTS_PREFIX = "comments:id:";
    private static final String COMMENTS_KEYSPACE = "Keyspace1";
    private static final String COMMENTS_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel COMMENTS_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel COMMENTS_CL_W = ConsistencyLevel.ALL;

    private static final String COMMENTS_EVENT_PREFIX = "comments:event:";
    private static final String COMMENTS_EVENT_KEYSPACE = "Keyspace1";
    private static final String COMMENTS_EVENT_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel COMMENTS_EVENT_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel COMMENTS_EVENT_CL_W = ConsistencyLevel.ALL;
    
    CommentCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    
    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public void addComment(PartakeConnection con, Comment embryo) throws DAOException {
        if (embryo == null) { throw new IllegalStateException(); }
        if (embryo.getId() == null) { throw new NullPointerException(); }
        
        try {
            CassandraConnection ccon = (CassandraConnection) con;
            addCommentToEvent(ccon.getClient(), embryo.getId(), embryo.getEventId(), ccon.getAcquiredTime());
            addCommentWithId(ccon.getClient(), embryo, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public Comment getComment(PartakeConnection con, String commentId) throws DAOException {
        try {
            CassandraConnection ccon = (CassandraConnection) con;
            return getCommentByIdImpl(ccon, commentId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void removeComment(PartakeConnection con, String commentId) throws DAOException {
        try {
            CassandraConnection ccon = (CassandraConnection) con;
            removeComment(ccon.getClient(), commentId, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }    
    
    @Override
    public DataIterator<Comment> getCommentsByEvent(PartakeConnection con, String eventId) throws DAOException {
        try {
            return getCommentsByEventImpl((CassandraConnection) con, eventId);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        removeAllData((CassandraConnection) con);
    }
    
    // ----------------------------------------------------------------------
    //

    private void addCommentWithId(Client client, Comment embryo, long time) throws Exception {
        String key = COMMENTS_PREFIX + embryo.getId();
        List<Mutation> mutations = new ArrayList<Mutation>(); 

        mutations.add(createMutation("userId", embryo.getUserId(), time));
        mutations.add(createMutation("eventId", embryo.getEventId(), time));
        mutations.add(createMutation("comment", embryo.getComment(), time));
        mutations.add(createMutation("createdAt", Util.getTimeString(embryo.getCreatedAt()), time));
        mutations.add(createMutation("deleted", "false", time));
        
        client.batch_mutate(COMMENTS_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(COMMENTS_COLUMNFAMILY, mutations)), COMMENTS_CL_W);
    }
    
    private void removeComment(Client client, String commentId, long time) throws Exception {
        String key = COMMENTS_PREFIX + commentId;
        
        ColumnPath columnPath = new ColumnPath(COMMENTS_COLUMNFAMILY);
        columnPath.setColumn(bytes("deleted"));
        
        client.insert(COMMENTS_KEYSPACE, key, columnPath, TRUE, time, COMMENTS_CL_W);    	
    }

    private void addCommentToEvent(Client client, String id, String eventId, long time) throws Exception {
        String key = COMMENTS_EVENT_PREFIX + eventId;
        
        ColumnPath columnPath = new ColumnPath(COMMENTS_EVENT_COLUMNFAMILY);
        columnPath.setColumn(bytes(Util.getTimeString(time) + id));
        
        client.insert(COMMENTS_EVENT_KEYSPACE, key, columnPath, bytes(id), time, COMMENTS_EVENT_CL_W);
    }
    
    private Comment getCommentByIdImpl(CassandraConnection ccon, String id) throws Exception {
        String key = COMMENTS_PREFIX + id;
        Client client = ccon.getClient();
        
        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange(new byte[0], new byte[0], false, 100);
        predicate.setSlice_range(sliceRange);

        ColumnParent parent = new ColumnParent(COMMENTS_COLUMNFAMILY);
        List<ColumnOrSuperColumn> results = client.get_slice(COMMENTS_KEYSPACE, key, parent, predicate, COMMENTS_CL_R);
        
        if (results == null || results.isEmpty()) { return null; }
        
        Comment comment = new Comment();
        comment.setId(id);
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            String name = string(column.getName()), value = string(column.getValue());
            if ("eventId".equals(name)) {
                comment.setEventId(value);
            } else if ("userId".equals(name)) {
                comment.setUserId(value);
            } else if ("comment".equals(string(column.getName()))) {
                comment.setComment(value);
            } else if ("createdAt".equals(string(column.getName()))) {
                comment.setCreatedAt(Util.dateFromTimeString(value));
            } else if ("deleted".equals(string(column.getName()))) {
                // deleted flag が立っている場合、null を返す。
                if ("true".equals(string(column.getValue()))) { return null; }
            }
        }
        
        // TODO: comment.validate() とか実装しておきたい。
        // 他のモデルに関しても validate() の実装を強制する
        
        return comment.freeze();
    }
    
    private CassandraColumnDataIterator<Comment> getCommentsByEventImpl(CassandraConnection connection, String eventId) throws Exception {
        String key = COMMENTS_EVENT_PREFIX + eventId;
        
        ColumnIterator iterator = new ColumnIterator(connection, factory, COMMENTS_KEYSPACE, key, COMMENTS_EVENT_COLUMNFAMILY, false, COMMENTS_EVENT_CL_R, COMMENTS_EVENT_CL_W); 
        
        return new CassandraColumnDataIterator<Comment>(iterator, new ColumnOrSuperColumnMapper<Comment>(connection, factory) {
            @Override
            public Comment map(ColumnOrSuperColumn cosc) throws DAOException {
                Column column = cosc.column;
                String commentId = string(column.value);
                return factory.getCommentAccess().getComment(connection, commentId);
            }

            @Override
            public ColumnOrSuperColumn unmap(Comment t) throws DAOException {
                throw new UnsupportedOperationException();
            }           
        });
    }
}
