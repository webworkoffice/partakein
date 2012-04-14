package in.partake.model.dao;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.access.IAccess;
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
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.access.IUserNotificationAccess;
import in.partake.model.dao.access.IUserPreferenceAccess;
import in.partake.model.dao.access.IUserReceivedMessageAccess;
import in.partake.model.dao.access.IUserSentMessageAccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PartakeDAOFactory implements IPartakeDAOs {
    private final ICalendarLinkageAccess calendarLinkageAccess;
    private final ICommentAccess commentAccess;
    private final IEnrollmentAccess enrollmentAccess;
    private final IEventAccess eventAccess;
    private final IEventReminderAccess eventReminderAccess;
    private final IEventFeedAccess eventFeedAccess;
    private final IEventActivityAccess eventActivityAccess;
    private final IImageAccess imageAccess;
    private final IOpenIDLinkageAccess openIDLinkageAccess;
    private final IThumbnailAccess thumbnailAccess;
    private final ITwitterLinkageAccess twitterLinkageAccess;
    private final IUserAccess userAccess;
    private final IUserPreferenceAccess userPreferenceAccess;

    private final IEventMessageAccess eventMessageAccess;
    private final IUserReceivedMessageAccess userMessageAccess;
    private final IEventNotificationAccess eventNotificationMessageAccess;
    private final IMessageAccess messageAccess;
    private final IMessageEnvelopeAccess messageEnvelopeAccess;
    private final ITwitterMessageAccess twitterMessageAccess;

    private final IUserNotificationAccess userNotificationAccess;
    private final IUserSentMessageAccess userSentMessageAccess;

    private final List<IAccess<?, ?>> daos;

    protected PartakeDAOFactory() {
        daos = new ArrayList<IAccess<?, ?>>();

        addDao(calendarLinkageAccess = createCalendarLinkageAccess());
        addDao(commentAccess         = createCommentAccess());
        addDao(enrollmentAccess      = createEnrollmentAccess());
        addDao(eventAccess           = createEventAccess());
        addDao(eventReminderAccess   = createEventReminderAccess());
        addDao(eventFeedAccess       = createEventFeedAccess());
        addDao(eventActivityAccess   = createEventActivityAccess());
        addDao(imageAccess           = createImageAccess());
        addDao(openIDLinkageAccess   = createOpenIDLinkageAccess());
        addDao(thumbnailAccess       = createThumbnailAccess());
        addDao(twitterLinkageAccess  = createTwitterLinkageAccess());
        addDao(userAccess            = creataeUserAccess());
        addDao(userPreferenceAccess  = createUserPreferenceAccess());
        addDao(eventMessageAccess = createEventMessageAccess());
        addDao(userMessageAccess = createUserReceivedMessageAccess());
        addDao(eventNotificationMessageAccess = createEventNotificationAccess());
        addDao(messageAccess = createMessageAccess());
        addDao(twitterMessageAccess = createTwitterMessageAccess());
        addDao(messageEnvelopeAccess = createMessageEnvelopeAccess());
        addDao(userNotificationAccess = createUserNotificationAccess());
        addDao(userSentMessageAccess = createUserSentMessageAccess());
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

    public final ICommentAccess getCommentAccess() {
        return commentAccess;
    }

    public final IEventReminderAccess getEventReminderAccess() {
        return eventReminderAccess;
    }

    public final IEnrollmentAccess getEnrollmentAccess() {
        return enrollmentAccess;
    }

    public final IEventAccess getEventAccess() {
        return eventAccess;
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

    public final IEventMessageAccess getEventMessageAccess() {
        return eventMessageAccess;
    }

    public final ICalendarLinkageAccess getCalendarLinkageAccess() {
        return calendarLinkageAccess;
    }

    public final IUserReceivedMessageAccess getUserReceivedMessageAccess() {
        return userMessageAccess;
    }

    public final IEventNotificationAccess getEventNotificationAccess() {
        return eventNotificationMessageAccess;
    }

    public final IMessageAccess getMessageAccess() {
        return messageAccess;
    }

    @Override
    public final IMessageEnvelopeAccess getMessageEnvelopeAccess() {
        return this.messageEnvelopeAccess;
    }

    @Override
    public final ITwitterMessageAccess getTwitterMessageAccess() {
        return this.twitterMessageAccess;
    }

    @Override
    public IUserNotificationAccess getUserNotificationAccess() {
        return this.userNotificationAccess;
    }

    @Override
    public IUserSentMessageAccess getUserSentMessageAccess() {
        return this.userSentMessageAccess;
    }

    protected abstract ICalendarLinkageAccess createCalendarLinkageAccess();
    protected abstract ICommentAccess createCommentAccess();
    protected abstract IEnrollmentAccess createEnrollmentAccess();
    protected abstract IEventAccess createEventAccess();
    protected abstract IEventReminderAccess createEventReminderAccess();
    protected abstract IEventFeedAccess createEventFeedAccess();
    protected abstract IEventActivityAccess createEventActivityAccess();
    protected abstract IOpenIDLinkageAccess createOpenIDLinkageAccess();
    protected abstract IImageAccess createImageAccess();
    protected abstract IThumbnailAccess createThumbnailAccess();
    protected abstract ITwitterLinkageAccess createTwitterLinkageAccess();
    protected abstract IUserAccess creataeUserAccess();
    protected abstract IUserPreferenceAccess createUserPreferenceAccess();
    protected abstract IEventMessageAccess createEventMessageAccess();
    protected abstract IUserReceivedMessageAccess createUserReceivedMessageAccess();
    protected abstract IEventNotificationAccess createEventNotificationAccess();
    protected abstract IMessageAccess createMessageAccess();
    protected abstract ITwitterMessageAccess createTwitterMessageAccess();
    protected abstract IMessageEnvelopeAccess createMessageEnvelopeAccess();
    protected abstract IUserNotificationAccess createUserNotificationAccess();
    protected abstract IUserSentMessageAccess createUserSentMessageAccess();
}
