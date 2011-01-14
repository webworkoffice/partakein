package in.partake.model.dao.cassandra;

import in.partake.model.dao.IBinaryAccess;
import in.partake.model.dao.ICacheAccess;
import in.partake.model.dao.ICalendarLinkageAccess;
import in.partake.model.dao.ICommentAccess;
import in.partake.model.dao.IDirectMessageAccess;
import in.partake.model.dao.IEnrollmentAccess;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.IEventRelationAccess;
import in.partake.model.dao.IFeedAccess;
import in.partake.model.dao.IOpenIDLinkageAccess;
import in.partake.model.dao.ITwitterLinkageAccess;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.IUserPreferenceAccess;
import in.partake.model.dao.PartakeDAOFactory;

public class CassandraDAOFactory extends PartakeDAOFactory {
    private final ICacheAccess cacheAccess;
    private final ICalendarLinkageAccess calendarLinkageAccess;    
    private final IBinaryAccess binaryAccess;
    private final ICommentAccess commentAccess;
    private final IDirectMessageAccess directMessageAccess;
    private final IEnrollmentAccess enrollmentAccess;
    private final IEventAccess eventAccess;
    private final IEventRelationAccess eventRelationAccess;
    private final IFeedAccess feedAccess;
    private final IOpenIDLinkageAccess openIDLinkageAccess;
    private final ITwitterLinkageAccess twitterLinkageAccess;
    private final IUserAccess userAccess;
    private final IUserPreferenceAccess userPreferenceAccess;
    
    public CassandraDAOFactory() {
        cacheAccess = new CassandraCacheDao(this);
        calendarLinkageAccess = new CalendarLinkageCassandraDao(this);
        binaryAccess = new BinaryCassandraDao(this);
        commentAccess = new CommentCassandraDao(this);
        directMessageAccess = new DirectMessageCassandraDao(this);
        enrollmentAccess = new EnrollmentCassandraDao(this);
        eventAccess = new EventCassandraDao(this);
        eventRelationAccess = new EventRelationCassandraDao(this);
        feedAccess = new FeedCassandraDao(this);
        openIDLinkageAccess = new OpenIDLinkageCassandraDao(this);
        twitterLinkageAccess = new TwitterLinkageCassandraDao(this);
        userAccess = new UserCassandraDao(this);
        userPreferenceAccess = new UserPreferenceCassandraDao(this);
    }
    
    @Override
    public ICacheAccess getCacheAccess() {
        return cacheAccess;
    }
    
    @Override
    public ICalendarLinkageAccess getCalendarAccess() {
        return calendarLinkageAccess;
    }
    
    @Override
    public IBinaryAccess getBinaryAccess() {
        return binaryAccess;
    }
    
    @Override
    public ICommentAccess getCommentAccess() {
        return commentAccess;
    }

    @Override
    public IDirectMessageAccess getDirectMessageAccess() {
        return directMessageAccess;
    }

    @Override
    public IEnrollmentAccess getEnrollmentAccess() {
        return enrollmentAccess;
    }

    @Override
    public IEventAccess getEventAccess() {
        return eventAccess;
    }
    
    @Override
    public IEventRelationAccess getEventRelationAccess() {
    	return eventRelationAccess;
    }

    @Override
    public IFeedAccess getFeedAccess() {
        return feedAccess;
    }

    @Override
    public IOpenIDLinkageAccess getOpenIDLinkageAccess() {
        return openIDLinkageAccess;
    }

    @Override
    public ITwitterLinkageAccess getTwitterLinkageAccess() {
        return twitterLinkageAccess;
    }

    @Override
    public IUserAccess getUserAccess() {
        return userAccess;
    }
    
    @Override
    public IUserPreferenceAccess getUserPreferenceAccess() {
        return userPreferenceAccess;
    }
}
