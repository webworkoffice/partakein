package in.partake.model.dao.cassandra;

import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IBinaryAccess;
import in.partake.model.dao.access.ICacheAccess;
import in.partake.model.dao.access.ICalendarLinkageAccess;
import in.partake.model.dao.access.ICommentAccess;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dao.access.IEnvelopeAccess;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dao.access.IEventRelationAccess;
import in.partake.model.dao.access.IEventReminderAccess;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dao.access.IQuestionnaireAccess;
import in.partake.model.dao.access.ITwitterLinkageAccess;
import in.partake.model.dao.access.IURLShortenerAccess;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.access.IUserPreferenceAccess;

public class CassandraDAOFactory extends PartakeDAOFactory {
    
    @Override
    protected ICacheAccess createCacheAccess() {
        return new CassandraCacheDao(this);
    }

    @Override
    protected ICalendarLinkageAccess createCalendarLinkageAccess() {
        return new CalendarLinkageCassandraDao(this);
    }

    @Override
    protected IBinaryAccess createBinaryAccess() {
        return new BinaryCassandraDao(this);
    }

    @Override
    protected ICommentAccess createCommentAccess() {
        return new CommentCassandraDao(this);
    }

    @Override
    protected IMessageAccess createDirectMessageAccess() {
        return new MessageCassandraDao(this);
    }

    @Override
    protected IEnrollmentAccess createEnrollmentAccess() {
        return new EnrollmentCassandraDao(this);
    }
    
    @Override
    protected IEnvelopeAccess createEnvelopeAccess() {
        return new CassandraEnvelopeDao(this);
    }

    @Override
    protected IEventAccess createEventAccess() {
        return new EventCassandraDao(this);
    }

    @Override
    protected IEventRelationAccess createEventRelationAccess() {
        return new EventRelationCassandraDao(this);
    }

    @Override
    protected IEventReminderAccess createEventReminderAccess() {
        return new CassandraEventReminderDao(this);
    }

    @Override
    protected IEventFeedAccess createEventFeedAccess() {
        return new EventFeedCassandraDao(this);
    }
    
    @Override
    protected IEventActivityAccess createEventActivityAccess() {
        return new CassandraEventActivityDao(this);
    }

    @Override
    protected IOpenIDLinkageAccess createOpenIDLinkageAccess() {
        return new OpenIDLinkageCassandraDao(this);
    }
    
    @Override
    protected IQuestionnaireAccess createQuestionnaireAccess() {
        return new CassandraQuestionnaireDao(this);
    }

    @Override
    protected ITwitterLinkageAccess createTwitterLinkageAccess() {
        return new TwitterLinkageCassandraDao(this);
    }

    @Override
    protected IUserAccess creataeUserAccess() {
        return new UserCassandraDao(this);
    }

    @Override
    protected IUserPreferenceAccess createUserPreferenceAccess() {
        return new UserPreferenceCassandraDao(this);

    }

    @Override
    protected IURLShortenerAccess createUrlShortenerAccess() {
        return new URLShortenerCassandraDao(this);
    }



}
