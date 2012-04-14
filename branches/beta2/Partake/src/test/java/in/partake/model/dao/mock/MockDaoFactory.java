package in.partake.model.dao.mock;

import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IBinaryAccess;
import in.partake.model.dao.access.ICalendarLinkageAccess;
import in.partake.model.dao.access.ICommentAccess;
import in.partake.model.dao.access.IDirectMessageAccess;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dao.access.IEnvelopeAccess;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dao.access.IEventMessageAccess;
import in.partake.model.dao.access.IEventNotificationAccess;
import in.partake.model.dao.access.IEventRelationAccess;
import in.partake.model.dao.access.IEventReminderAccess;
import in.partake.model.dao.access.IImageAccess;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dao.access.IThumbnailAccess;
import in.partake.model.dao.access.ITwitterLinkageAccess;
import in.partake.model.dao.access.IURLShortenerAccess;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.access.IUserMessageAccess;
import in.partake.model.dao.access.IUserPreferenceAccess;

import org.mockito.Mockito;

public class MockDaoFactory extends PartakeDAOFactory {

    public MockDaoFactory() {
    }

    @Override
    protected ICalendarLinkageAccess createCalendarLinkageAccess() {
        return Mockito.mock(ICalendarLinkageAccess.class);
    }

    @Override
    protected IBinaryAccess createBinaryAccess() {
        return Mockito.mock(IBinaryAccess.class);
    }

    @Override
    protected ICommentAccess createCommentAccess() {
        return Mockito.mock(ICommentAccess.class);
    }

    @Override
    protected IDirectMessageAccess createDirectMessageAccess() {
        return Mockito.mock(IDirectMessageAccess.class);
    }

    @Override
    protected IEnrollmentAccess createEnrollmentAccess() {
        return Mockito.mock(IEnrollmentAccess.class);
    }

    @Override
    protected IEventAccess createEventAccess() {
        return Mockito.mock(IEventAccess.class);
    }

    @Override
    public IEventMessageAccess createEventMessageAccess() {
        return Mockito.mock(IEventMessageAccess.class);
    }

    @Override
    public IEventNotificationAccess createEventNotificationAccess() {
        return Mockito.mock(IEventNotificationAccess.class);
    }

    @Override
    protected IEventRelationAccess createEventRelationAccess() {
        return Mockito.mock(IEventRelationAccess.class);
    }

    @Override
    protected IEventFeedAccess createEventFeedAccess() {
        return Mockito.mock(IEventFeedAccess.class);
    }

    @Override
    protected IEventActivityAccess createEventActivityAccess() {
        return Mockito.mock(IEventActivityAccess.class);
    }

    @Override
    protected IOpenIDLinkageAccess createOpenIDLinkageAccess() {
        return Mockito.mock(IOpenIDLinkageAccess.class);
    }

    @Override
    protected IThumbnailAccess createThumbnailAccess() {
        return Mockito.mock(IThumbnailAccess.class);
    }

    @Override
    protected ITwitterLinkageAccess createTwitterLinkageAccess() {
        return Mockito.mock(ITwitterLinkageAccess.class);
    }

    @Override
    protected IUserAccess creataeUserAccess() {
        return Mockito.mock(IUserAccess.class);
    }

    @Override
    protected IUserPreferenceAccess createUserPreferenceAccess() {
        return Mockito.mock(IUserPreferenceAccess.class);
    }

    @Override
    protected IURLShortenerAccess createUrlShortenerAccess() {
        return Mockito.mock(IURLShortenerAccess.class);
    }

    @Override
    protected IEnvelopeAccess createEnvelopeAccess() {
        return Mockito.mock(IEnvelopeAccess.class);
    }

    @Override
    protected IEventReminderAccess createEventReminderAccess() {
        return Mockito.mock(IEventReminderAccess.class);
    }

    @Override
    protected IImageAccess createImageAccess() {
        return Mockito.mock(IImageAccess.class);
    }

    @Override
    public IUserMessageAccess createUserMessageAccess() {
        return Mockito.mock(IUserMessageAccess.class);
    }

    @Override
    public IMessageAccess createMessageAccess() {
        return Mockito.mock(IMessageAccess.class);
    }
}
