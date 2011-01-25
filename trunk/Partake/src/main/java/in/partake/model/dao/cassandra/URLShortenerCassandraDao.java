package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.string;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.IURLShortenerAccess;
import in.partake.model.dao.PartakeConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.Cassandra.Client;

public class URLShortenerCassandraDao extends CassandraDao implements IURLShortenerAccess {
    // MASTER TABLE
    private static final String URLSHORTENER_PREFIX = "urlshortener:id:";
    private static final String URLSHORTENER_KEYSPACE = "Keyspace1";
    private static final String URLSHORTENER_COLUMNFAMILY = "Standard2";
    private static final ConsistencyLevel URLSHORTENER_CL_R = ConsistencyLevel.ONE;
    private static final ConsistencyLevel URLSHORTENER_CL_W = ConsistencyLevel.ALL;
    
    public URLShortenerCassandraDao(CassandraDAOFactory factory) {
        super(factory);
    }
    
    @Override
    public void addShortenedURL(PartakeConnection con, String originalURL, String type, String shortenedURL) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addShortenedURLImpl(ccon.getClient(), originalURL, type, shortenedURL, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public String getShortenedURL(PartakeConnection con, String originalURL, String type) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return getShortenedURL(ccon.getClient(), originalURL, type, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public String getShortenedURL(PartakeConnection con, String originalURL) throws DAOException {        
        return getShortenedURL(con, originalURL, null);
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet");
    }
    
    // ----------------------------------------------------------------------
    
    private void addShortenedURLImpl(Client client, String originalURL, String type, String shortenedURL, long time) throws Exception {
        String key = URLSHORTENER_PREFIX + originalURL;

        List<Mutation> mutations = new ArrayList<Mutation>(); 
        mutations.add(createMutation(type, shortenedURL, time));
        
        client.batch_mutate(URLSHORTENER_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(URLSHORTENER_COLUMNFAMILY, mutations)), URLSHORTENER_CL_W);
    }
    
    private String getShortenedURL(Client client, String originalURL, String type, long time) throws Exception {
        String key = URLSHORTENER_PREFIX + originalURL;

        List<ColumnOrSuperColumn> results = getSlice(client, URLSHORTENER_KEYSPACE, URLSHORTENER_COLUMNFAMILY, key, URLSHORTENER_CL_R);
        if (results == null || results.isEmpty()) { return null; }
        
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            if (column == null) { continue; }
            String name = string(column.getName());
            String value = string(column.getValue());
            
            if (type == null) { return value; }
            if (type.equals(name)) { return value; }
        }
        
        return null;
    }
}
