package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.ICalendarLinkageAccess;
import in.partake.model.dao.access.ICommentAccess;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dao.access.IEventMessageAccess;
import in.partake.model.dao.access.IEventTicketAccess;
import in.partake.model.dao.access.IEventTicketNotificationAccess;
import in.partake.model.dao.access.IImageAccess;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dao.access.IMessageEnvelopeAccess;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dao.access.IThumbnailAccess;
import in.partake.model.dao.access.ITwitterLinkageAccess;
import in.partake.model.dao.access.ITwitterMessageAccess;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.access.IUserNotificationAccess;
import in.partake.model.dao.access.IUserPreferenceAccess;
import in.partake.model.dao.access.IUserReceivedMessageAccess;
import in.partake.model.dao.access.IUserSentMessageAccess;
import in.partake.model.dao.postgres9.impl.Postgres9CalendarLinkageDao;
import in.partake.model.dao.postgres9.impl.Postgres9CommentDao;
import in.partake.model.dao.postgres9.impl.Postgres9EnrollmentDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventActivityDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventFeedDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventMessageDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventNotificationDao;
import in.partake.model.dao.postgres9.impl.Postgres9EventTicketDao;
import in.partake.model.dao.postgres9.impl.Postgres9ImageDao;
import in.partake.model.dao.postgres9.impl.Postgres9MessageDao;
import in.partake.model.dao.postgres9.impl.Postgres9MessageEnvelopeDao;
import in.partake.model.dao.postgres9.impl.Postgres9OpenIDLinkageDao;
import in.partake.model.dao.postgres9.impl.Postgres9ThumbnailDao;
import in.partake.model.dao.postgres9.impl.Postgres9TwitterLinkageDao;
import in.partake.model.dao.postgres9.impl.Postgres9TwitterMessageDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserNotificationDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserPreferenceDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserReceivedMessageDao;
import in.partake.model.dao.postgres9.impl.Postgres9UserSentMessageDao;

public class Postgres9DAOFactory extends PartakeDAOFactory {
    public Postgres9DAOFactory() {
        super();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        super.initialize(con);
    }

    @Override
    protected ICalendarLinkageAccess createCalendarLinkageAccess() {
        return new Postgres9CalendarLinkageDao();
    }

    @Override
    protected ICommentAccess createCommentAccess() {
        return new Postgres9CommentDao();
    }

    @Override
    protected IEnrollmentAccess createEnrollmentAccess() {
        return new Postgres9EnrollmentDao();
    }

    @Override
    protected IEventAccess createEventAccess() {
        return new Postgres9EventDao();
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
    public IEventTicketAccess createEventTicketAccess() {
        return new Postgres9EventTicketDao();
    }

    @Override
    protected IOpenIDLinkageAccess createOpenIDLinkageAccess() {
        return new Postgres9OpenIDLinkageDao();
    }

    @Override
    protected IImageAccess createImageAccess() {
        return new Postgres9ImageDao();
    }

    @Override
    protected IThumbnailAccess createThumbnailAccess() {
        return new Postgres9ThumbnailDao();
    }

    @Override
    protected ITwitterLinkageAccess createTwitterLinkageAccess() {
        return new Postgres9TwitterLinkageDao();
    }

    @Override
    protected IMessageAccess createMessageAccess() {
        return new Postgres9MessageDao();
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
    protected IEventMessageAccess createEventMessageAccess() {
        return new Postgres9EventMessageDao();
    }

    @Override
    protected IUserReceivedMessageAccess createUserReceivedMessageAccess() {
        return new Postgres9UserReceivedMessageDao();
    }

    @Override
    protected IEventTicketNotificationAccess createEventNotificationAccess() {
        return new Postgres9EventNotificationDao();
    }

    @Override
    protected IMessageEnvelopeAccess createMessageEnvelopeAccess() {
        return new Postgres9MessageEnvelopeDao();
    }

    @Override
    protected ITwitterMessageAccess createTwitterMessageAccess() {
        return new Postgres9TwitterMessageDao();
    }

    @Override
    protected IUserNotificationAccess createUserNotificationAccess() {
        return new Postgres9UserNotificationDao();
    }

    @Override
    protected IUserSentMessageAccess createUserSentMessageAccess() {
        return new Postgres9UserSentMessageDao();
    }
}
