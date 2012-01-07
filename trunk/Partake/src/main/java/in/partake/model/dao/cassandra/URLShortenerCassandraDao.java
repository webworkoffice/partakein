package in.partake.model.dao.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.string;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IURLShortenerAccess;
import in.partake.model.dto.ShortenedURLData;
import in.partake.model.dto.pk.ShortenedURLDataPK;

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
    public void put(PartakeConnection con, ShortenedURLData data) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addShortenedURLImpl(ccon.getClient(), data.getOriginalURL(), data.getServiceType(), data.getShortenedURL(), ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public ShortenedURLData find(PartakeConnection con, ShortenedURLDataPK pk) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            return findImpl(ccon.getClient(), pk.getOriginalURL(), pk.getServiceType(), ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public void remove(PartakeConnection con, ShortenedURLDataPK pk) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            addShortenedURLImpl(ccon.getClient(), pk.getOriginalURL(), pk.getServiceType(), null, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public DataIterator<ShortenedURLData> getIterator(PartakeConnection con) throws DAOException {
        return new CassandraKeyColumnDataIterator<ShortenedURLData>((CassandraConnection) con,
                new CassandraTableDescription(URLSHORTENER_PREFIX, URLSHORTENER_KEYSPACE, URLSHORTENER_COLUMNFAMILY, URLSHORTENER_CL_R, URLSHORTENER_CL_W),
                new URLShortenerMapper((CassandraConnection) con, factory));
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        removeAllData((CassandraConnection) con);
    }

    @Override
    public ShortenedURLData findByURL(PartakeConnection con, String originalURL) throws DAOException {        
        return find(con, new ShortenedURLDataPK(originalURL, null));
    }
    
    @Override
    public void removeByURL(PartakeConnection con, String originalURL) throws DAOException {
        CassandraConnection ccon = (CassandraConnection) con;
        try {
            removeShortenedURLAllImpl(ccon, originalURL, ccon.getAcquiredTime());
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }    

    // ----------------------------------------------------------------------
    
    private void addShortenedURLImpl(Client client, String originalURL, String type, String shortenedURL, long time) throws Exception {
        String key = URLSHORTENER_PREFIX + originalURL;

        List<Mutation> mutations = new ArrayList<Mutation>(); 
        mutations.add(createMutation(type, shortenedURL, time));
        
        client.batch_mutate(URLSHORTENER_KEYSPACE, Collections.singletonMap(key, Collections.singletonMap(URLSHORTENER_COLUMNFAMILY, mutations)), URLSHORTENER_CL_W);
    }
    
    private ShortenedURLData findImpl(Client client, String originalURL, String type, long time) throws Exception {
        String key = URLSHORTENER_PREFIX + originalURL;

        List<ColumnOrSuperColumn> results = getSlice(client, URLSHORTENER_KEYSPACE, URLSHORTENER_COLUMNFAMILY, key, URLSHORTENER_CL_R);
        if (results == null || results.isEmpty()) { return null; }
        
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            if (column == null) { continue; }
            String name = string(column.getName());
            String value = string(column.getValue());
            
            if (type == null) { return new ShortenedURLData(originalURL, name, value).freeze(); }
            if (type.equals(name)) { return new ShortenedURLData(originalURL, name, value).freeze(); }
        }
        
        return null;
    }
    
    private void removeShortenedURLAllImpl(CassandraConnection con, String originalURL, long time) throws DAOException {
        String key = URLSHORTENER_PREFIX + originalURL;
        ColumnIterator it = new ColumnIterator(con, URLSHORTENER_KEYSPACE, key, URLSHORTENER_COLUMNFAMILY, false, URLSHORTENER_CL_R, URLSHORTENER_CL_W);
        while (it.hasNext()) { 
            it.next();
            it.remove();
        }
    }
}


class URLShortenerMapper extends ColumnOrSuperColumnKeyMapper<ShortenedURLData> {
    
    public URLShortenerMapper(CassandraConnection con, CassandraDAOFactory factory) {
        // TODO Auto-generated constructor stub
        super(con, factory);
    }
    
    @Override
    public ShortenedURLData map(ColumnOrSuperColumn cosc, String key) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public ColumnOrSuperColumn unmap(ShortenedURLData t, long time) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }
}
