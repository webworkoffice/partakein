package in.partake.model;

import in.partake.model.dao.access.ICalendarLinkageAccess;
import in.partake.model.dao.access.ICommentAccess;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dao.access.IEventMessageAccess;
import in.partake.model.dao.access.IEventNotificationAccess;
import in.partake.model.dao.access.IEventReminderAccess;
import in.partake.model.dao.access.IImageAccess;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dao.access.IMessageEnvelopeAccess;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dao.access.IThumbnailAccess;
import in.partake.model.dao.access.ITwitterLinkageAccess;
import in.partake.model.dao.access.ITwitterMessageAccess;
import in.partake.model.dao.access.IURLShortenerAccess;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.access.IUserNotificationAccess;
import in.partake.model.dao.access.IUserPreferenceAccess;
import in.partake.model.dao.access.IUserReceivedMessageAccess;
import in.partake.model.dao.access.IUserSentMessageAccess;

public interface IPartakeDAOs {
    public ICalendarLinkageAccess getCalendarAccess();
    public ICommentAccess getCommentAccess();
    public IEnrollmentAccess getEnrollmentAccess();
    public IEventAccess getEventAccess();
    public IEventActivityAccess getEventActivityAccess();
    public IEventFeedAccess getEventFeedAccess();
    public IEventMessageAccess getEventMessageAccess();
    public IEventNotificationAccess getEventNotificationAccess();
    public IEventReminderAccess getEventReminderAccess();
    public IImageAccess getImageAccess();
    public IMessageAccess getMessageAccess();
    public IMessageEnvelopeAccess getMessageEnvelopeAccess();
    public IOpenIDLinkageAccess getOpenIDLinkageAccess();
    public IThumbnailAccess getThumbnailAccess();
    public ITwitterLinkageAccess getTwitterLinkageAccess();
    public ITwitterMessageAccess getTwitterMessageAccess();
    public IUserAccess getUserAccess();
    public IUserReceivedMessageAccess getUserReceivedMessageAccess();
    public IUserSentMessageAccess getUserSentMessageAccess();
    public IUserNotificationAccess getUserNotificationAccess();
    public IUserPreferenceAccess getUserPreferenceAccess();
    public IURLShortenerAccess getURLShortenerAccess();
}
