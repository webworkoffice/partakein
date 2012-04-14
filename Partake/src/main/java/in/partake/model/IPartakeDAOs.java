package in.partake.model;

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

public interface IPartakeDAOs {
    public IBinaryAccess getBinaryAccess();
    public ICalendarLinkageAccess getCalendarAccess();
    public ICommentAccess getCommentAccess();
    public IDirectMessageAccess getDirectMessageAccess();
    public IEventReminderAccess getEventReminderAccess();
    public IEnvelopeAccess getEnvelopeAccess();
    public IEnrollmentAccess getEnrollmentAccess();
    public IEventAccess getEventAccess();
    public IEventActivityAccess getEventActivityAccess();
    public IEventFeedAccess getEventFeedAccess();
    public IEventMessageAccess getEventMessageAccess();
    public IEventNotificationAccess getEventNotificationMessageAccess();
    public IEventRelationAccess getEventRelationAccess();
    public IImageAccess getImageAccess();
    public IMessageAccess getMessageAccess();
    public IOpenIDLinkageAccess getOpenIDLinkageAccess();
    public IThumbnailAccess getThumbnailAccess();
    public ITwitterLinkageAccess getTwitterLinkageAccess();
    public IUserAccess getUserAccess();
    public IUserMessageAccess getUserMessageAccess();
    public IUserPreferenceAccess getUserPreferenceAccess();
    public IURLShortenerAccess getURLShortenerAccess();

}
