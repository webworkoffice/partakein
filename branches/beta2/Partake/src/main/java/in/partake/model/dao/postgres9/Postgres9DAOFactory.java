package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
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
import in.partake.model.dao.access.ITwitterLinkageAccess;
import in.partake.model.dao.access.IURLShortenerAccess;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.access.IUserPreferenceAccess;
import in.partake.model.dao.postgres9.impl.Postgres9BinaryDao;
import in.partake.model.dao.postgres9.impl.Postgres9CacheDao;
import in.partake.model.dao.postgres9.impl.Postgres9CalendarLinkageDao;
import in.partake.model.dao.postgres9.impl.Postgres9CommentDao;
import in.partake.model.dao.postgres9.impl.Postgres9DirectMessageDao;
import in.partake.model.dao.postgres9.impl.Postgres9EnrollmentDao;
import in.partake.model.dao.postgres9.impl.Postgres9EnvelopeDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventActivityDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventFeedDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventRelationDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventReminderDao;
import in.partake.model.dao.postgres9.impl.Postgres9OpenIDLinkageDao;
import in.partake.model.dao.postgres9.impl.Postgres9TwitterLinkageDao;
import in.partake.model.dao.postgres9.impl.Postgres9UrlShortenerDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserPreferenceDao;

public class Postgres9DAOFactory extends PartakeDAOFactory {
    public Postgres9DAOFactory() {
        super();
    }
    
    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        super.initialize(con);
    }
    
    @Override
    protected ICacheAccess createCacheAccess() {
        return new Postgres9CacheDao();
    }

    @Override
    protected ICalendarLinkageAccess createCalendarLinkageAccess() {
        return new Postgres9CalendarLinkageDao();
    }

    @Override
    protected IBinaryAccess createBinaryAccess() {
        return new Postgres9BinaryDao();
    }

    @Override
    protected ICommentAccess createCommentAccess() {
        return new Postgres9CommentDao();
    }

    @Override
    protected IMessageAccess createDirectMessageAccess() {
        return new Postgres9DirectMessageDao();
    }

    @Override
    protected IEnrollmentAccess createEnrollmentAccess() {
        return new Postgres9EnrollmentDao();
    }

    @Override
    protected IEnvelopeAccess createEnvelopeAccess() {
        return new Postgres9EnvelopeDao();
    }

    @Override
    protected IEventAccess createEventAccess() {
        return new Postgres9EventDao();
    }

    @Override
    protected IEventRelationAccess createEventRelationAccess() {
        return new Postgres9EventRelationDao();
    }

    @Override
    protected IEventReminderAccess createEventReminderAccess() {
        return new Postgres9EventReminderDao();
    }

    @Override
    protected IEventFeedAccess createEventFeedAccess() {
        return new Postgres9EventFeedDao();
    }

    @Override
    protected IEventActivityAccess createEventActivityAccess() {
        return new Postgres9EventActivityDao();
    }

    @Override
    protected IOpenIDLinkageAccess createOpenIDLinkageAccess() {
        return new Postgres9OpenIDLinkageDao();
    }

    @Override
    protected ITwitterLinkageAccess createTwitterLinkageAccess() {
        return new Postgres9TwitterLinkageDao();
    }

    @Override
    protected IUserAccess creataeUserAccess() {
        return new Postgres9UserDao();
    }

    @Override
    protected IUserPreferenceAccess createUserPreferenceAccess() {
        return new Postgres9UserPreferenceDao();
    }

    @Override
    protected IURLShortenerAccess createUrlShortenerAccess() {
        return new Postgres9UrlShortenerDao();
    }

}
