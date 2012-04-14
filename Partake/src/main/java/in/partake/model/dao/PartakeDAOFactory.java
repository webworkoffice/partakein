package in.partake.model.dao;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.access.IAccess;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PartakeDAOFactory implements IPartakeDAOs {
    private final ICalendarLinkageAccess calendarLinkageAccess;
    private final IBinaryAccess binaryAccess;
    private final ICommentAccess commentAccess;
    private final IDirectMessageAccess directMessageAccess;
    private final IEnrollmentAccess enrollmentAccess;
    private final IEnvelopeAccess envelopeAccess;
    private final IEventAccess eventAccess;
    private final IEventRelationAccess eventRelationAccess;
    private final IEventReminderAccess eventReminderAccess;
    private final IEventFeedAccess eventFeedAccess;
    private final IEventActivityAccess eventActivityAccess;
    private final IImageAccess imageAccess;
    private final IOpenIDLinkageAccess openIDLinkageAccess;
    private final IThumbnailAccess thumbnailAccess;
    private final ITwitterLinkageAccess twitterLinkageAccess;
    private final IUserAccess userAccess;
    private final IUserPreferenceAccess userPreferenceAccess;
    private final IURLShortenerAccess urlShortenerAccess;

    private final IEventMessageAccess eventMessageAccess;
    private final IUserMessageAccess userMessageAccess;
    private final IEventNotificationAccess eventNotificationMessageAccess;
    private final IMessageAccess messageAccess;


    private final List<IAccess<?, ?>> daos;

    protected PartakeDAOFactory() {
        daos = new ArrayList<IAccess<?, ?>>();

        addDao(calendarLinkageAccess = createCalendarLinkageAccess());
        addDao(binaryAccess          = createBinaryAccess());
        addDao(commentAccess         = createCommentAccess());
        addDao(directMessageAccess   = createDirectMessageAccess());
        addDao(envelopeAccess        = createEnvelopeAccess());
        addDao(enrollmentAccess      = createEnrollmentAccess());
        addDao(eventAccess           = createEventAccess());
        addDao(eventRelationAccess   = createEventRelationAccess());
        addDao(eventReminderAccess   = createEventReminderAccess());
        addDao(eventFeedAccess       = createEventFeedAccess());
        addDao(eventActivityAccess   = createEventActivityAccess());
        addDao(imageAccess           = createImageAccess());
        addDao(openIDLinkageAccess   = createOpenIDLinkageAccess());
        addDao(thumbnailAccess       = createThumbnailAccess());
        addDao(twitterLinkageAccess  = createTwitterLinkageAccess());
        addDao(userAccess            = creataeUserAccess());
        addDao(userPreferenceAccess  = createUserPreferenceAccess());
        addDao(urlShortenerAccess    = createUrlShortenerAccess());
        addDao(eventMessageAccess = createEventMessageAccess());
        addDao(userMessageAccess = createUserMessageAccess());
        addDao(eventNotificationMessageAccess = createEventNotificationAccess());
        addDao(messageAccess = createMessageAccess());
    }

    public void initialize(PartakeConnection con) throws DAOException {
        for (IAccess<?, ?> dao : daos) {
            dao.initialize(con);
        }
    }

    // ----------------------------------------------------------------------
    //

    private void addDao(IAccess<?, ?> t) {
        if (t != null)
            daos.add(t);
    }

    public List<IAccess<?, ?>> getDaos() {
        return Collections.unmodifiableList(daos);
    }


    // ----------------------------------------------------------------------
    // accessors

    public final ICalendarLinkageAccess getCalendarAccess() {
        return calendarLinkageAccess;
    }

    public final IBinaryAccess getBinaryAccess() {
        return binaryAccess;
    }

    public final ICommentAccess getCommentAccess() {
        return commentAccess;
    }

    public final IDirectMessageAccess getDirectMessageAccess() {
        return directMessageAccess;
    }

    public final IEventReminderAccess getEventReminderAccess() {
        return eventReminderAccess;
    }

    public final IEnvelopeAccess getEnvelopeAccess() {
        return envelopeAccess;
    }

    public final IEnrollmentAccess getEnrollmentAccess() {
        return enrollmentAccess;
    }

    public final IEventAccess getEventAccess() {
        return eventAccess;
    }

    public final IEventRelationAccess getEventRelationAccess() {
        return eventRelationAccess;
    }

    public final IEventFeedAccess getEventFeedAccess() {
        return eventFeedAccess;
    }

    public final IEventActivityAccess getEventActivityAccess() {
        return eventActivityAccess;
    }

    public final IImageAccess getImageAccess() {
        return imageAccess;
    }

    public final IOpenIDLinkageAccess getOpenIDLinkageAccess() {
        return openIDLinkageAccess;
    }

    public final IThumbnailAccess getThumbnailAccess() {
        return thumbnailAccess;
    }

    public final ITwitterLinkageAccess getTwitterLinkageAccess() {
        return twitterLinkageAccess;
    }

    public final IUserAccess getUserAccess() {
        return userAccess;
    }

    public final IUserPreferenceAccess getUserPreferenceAccess() {
        return userPreferenceAccess;
    }

    public final IURLShortenerAccess getURLShortenerAccess() {
        return urlShortenerAccess;
    }

    public final IEventMessageAccess getEventMessageAccess() {
        return eventMessageAccess;
    }

    public final ICalendarLinkageAccess getCalendarLinkageAccess() {
        return calendarLinkageAccess;
    }

    public final IURLShortenerAccess getUrlShortenerAccess() {
        return urlShortenerAccess;
    }

    public final IUserMessageAccess getUserMessageAccess() {
        return userMessageAccess;
    }

    public final IEventNotificationAccess getEventNotificationMessageAccess() {
        return eventNotificationMessageAccess;
    }

    public final IMessageAccess getMessageAccess() {
        return messageAccess;
    }

    protected abstract ICalendarLinkageAccess createCalendarLinkageAccess();
    protected abstract IBinaryAccess createBinaryAccess();
    protected abstract ICommentAccess createCommentAccess();
    protected abstract IDirectMessageAccess createDirectMessageAccess();
    protected abstract IEnrollmentAccess createEnrollmentAccess();
    protected abstract IEnvelopeAccess createEnvelopeAccess();
    protected abstract IEventAccess createEventAccess();
    protected abstract IEventRelationAccess createEventRelationAccess();
    protected abstract IEventReminderAccess createEventReminderAccess();
    protected abstract IEventFeedAccess createEventFeedAccess();
    protected abstract IEventActivityAccess createEventActivityAccess();
    protected abstract IOpenIDLinkageAccess createOpenIDLinkageAccess();
    protected abstract IImageAccess createImageAccess();
    protected abstract IThumbnailAccess createThumbnailAccess();
    protected abstract ITwitterLinkageAccess createTwitterLinkageAccess();
    protected abstract IUserAccess creataeUserAccess();
    protected abstract IUserPreferenceAccess createUserPreferenceAccess();
    protected abstract IURLShortenerAccess createUrlShortenerAccess();
    protected abstract IEventMessageAccess createEventMessageAccess();
    protected abstract IUserMessageAccess createUserMessageAccess();
    protected abstract IEventNotificationAccess createEventNotificationAccess();
    protected abstract IMessageAccess createMessageAccess();
}
