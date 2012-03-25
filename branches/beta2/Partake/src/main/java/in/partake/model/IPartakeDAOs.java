package in.partake.model;

import in.partake.model.dao.access.IBinaryAccess;
import in.partake.model.dao.access.ICalendarLinkageAccess;
import in.partake.model.dao.access.ICommentAccess;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dao.access.IEnvelopeAccess;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dao.access.IEventRelationAccess;
import in.partake.model.dao.access.IEventReminderAccess;
import in.partake.model.dao.access.IImageAccess;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dao.access.ITwitterLinkageAccess;
import in.partake.model.dao.access.IURLShortenerAccess;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.access.IUserPreferenceAccess;

public interface IPartakeDAOs {
    public ICalendarLinkageAccess getCalendarAccess();
    public IBinaryAccess getBinaryAccess();
    public ICommentAccess getCommentAccess();
    public IMessageAccess getDirectMessageAccess();
    public IEventReminderAccess getEventReminderAccess();
    public IEnvelopeAccess getEnvelopeAccess();
    public IEnrollmentAccess getEnrollmentAccess();
    public IEventAccess getEventAccess();
    public IEventRelationAccess getEventRelationAccess();
    public IEventFeedAccess getEventFeedAccess();
    public IEventActivityAccess getEventActivityAccess();
    public IImageAccess getImageAccess();
    public IOpenIDLinkageAccess getOpenIDLinkageAccess();
    public ITwitterLinkageAccess getTwitterLinkageAccess();
    public IUserAccess getUserAccess();
    public IUserPreferenceAccess getUserPreferenceAccess();
    public IURLShortenerAccess getURLShortenerAccess();
}
