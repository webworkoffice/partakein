package in.partake.model.dao.cassandra;

import java.util.Date;

import org.apache.log4j.Logger;

import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.service.CassandraClientPoolFactory;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IBinaryAccess;
import in.partake.model.dao.ICalendarLinkageAccess;
import in.partake.model.dao.ICommentAccess;
import in.partake.model.dao.IDirectMessageAccess;
import in.partake.model.dao.IEnrollmentAccess;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.IFeedAccess;
import in.partake.model.dao.IMessageAccess;
import in.partake.model.dao.IOpenIDLinkageAccess;
import in.partake.model.dao.ITwitterLinkageAccess;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.IUserPreferenceAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.resource.PartakeProperties;

public class CassandraDAOFactory extends PartakeDAOFactory {
    private static final Logger logger = Logger.getLogger(CassandraDAOFactory.class);

    @Override
    public PartakeCassandraConnection getConnection() throws DAOException {
        try {
            long now = new Date().getTime();
            CassandraClientPool pool = CassandraClientPoolFactory.INSTANCE.get();
            
            String host = PartakeProperties.get().getCassandraHost();
            int port = PartakeProperties.get().getCassandraPort();
            
            CassandraClient client = pool.borrowClient(host, port);
            return new PartakeCassandraConnection(client, now);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public ICalendarLinkageAccess getCalendarAccess() {
        return new CalendarLinkageCassandraDao();
    }
    
    @Override
    public IBinaryAccess getBinaryAccess() {
        return new BinaryCassandraDao();
    }
    
    @Override
    public ICommentAccess getCommentAccess() {
        return new CommentCassandraDao();
    }

    @Override
    public IDirectMessageAccess getDirectMessageAccess() {
        return new DirectMessageCassandraDao();
    }

    @Override
    public IEnrollmentAccess getEnrollmentAccess() {
        return new EnrollmentCassandraDao();
    }

    @Override
    public IEventAccess getEventAccess() {
        return new EventCassandraDao();
    }

    @Override
    public IFeedAccess getFeedAccess() {
        return new FeedCassandraDao();
    }

    @Override
    public IMessageAccess getMessageAccess() {
        return new MessageCassandraDao();
    }

    @Override
    public IOpenIDLinkageAccess getOpenIDLinkageAccess() {
        return new OpenIDLinkageCassandraDao();
    }

    @Override
    public ITwitterLinkageAccess getTwitterLinkageAccess() {
        return new TwitterLinkageCassandraDao();
    }

    @Override
    public IUserAccess getUserAccess() {
        return new UserCassandraDao();
    }
    
    @Override
    public IUserPreferenceAccess getUserPreferenceAccess() {
        return new UserPreferenceCassandraDao();
    }
}
