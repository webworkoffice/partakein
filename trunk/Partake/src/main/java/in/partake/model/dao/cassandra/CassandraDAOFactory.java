package in.partake.model.dao.cassandra;

import in.partake.model.dao.IBinaryAccess;
import in.partake.model.dao.ICalendarLinkageAccess;
import in.partake.model.dao.ICommentAccess;
import in.partake.model.dao.IDirectMessageAccess;
import in.partake.model.dao.IEnrollmentAccess;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.IEventRelationAccess;
import in.partake.model.dao.IFeedAccess;
import in.partake.model.dao.IMessageAccess;
import in.partake.model.dao.IOpenIDLinkageAccess;
import in.partake.model.dao.ITwitterLinkageAccess;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.IUserPreferenceAccess;
import in.partake.model.dao.PartakeDAOFactory;

public class CassandraDAOFactory extends PartakeDAOFactory {
    @Override
    public ICalendarLinkageAccess getCalendarAccess() {
        return new CalendarLinkageCassandraDao(this);
    }
    
    @Override
    public IBinaryAccess getBinaryAccess() {
        return new BinaryCassandraDao(this);
    }
    
    @Override
    public ICommentAccess getCommentAccess() {
        return new CommentCassandraDao(this);
    }

    @Override
    public IDirectMessageAccess getDirectMessageAccess() {
        return new DirectMessageCassandraDao(this);
    }

    @Override
    public IEnrollmentAccess getEnrollmentAccess() {
        return new EnrollmentCassandraDao(this);
    }

    @Override
    public IEventAccess getEventAccess() {
        return new EventCassandraDao(this);
    }
    
    @Override
    public IEventRelationAccess getEventRelationAccess() {
    	return new EventRelationCassandraDao(this);
    }

    @Override
    public IFeedAccess getFeedAccess() {
        return new FeedCassandraDao(this);
    }

    @Override
    public IMessageAccess getMessageAccess() {
        return new MessageCassandraDao(this);
    }

    @Override
    public IOpenIDLinkageAccess getOpenIDLinkageAccess() {
        return new OpenIDLinkageCassandraDao(this);
    }

    @Override
    public ITwitterLinkageAccess getTwitterLinkageAccess() {
        return new TwitterLinkageCassandraDao(this);
    }

    @Override
    public IUserAccess getUserAccess() {
        return new UserCassandraDao(this);
    }
    
    @Override
    public IUserPreferenceAccess getUserPreferenceAccess() {
        return new UserPreferenceCassandraDao(this);
    }
}
