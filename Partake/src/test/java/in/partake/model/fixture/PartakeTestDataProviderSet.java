package in.partake.model.fixture;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.fixture.impl.CalendarLinkageTestDataProvider;
import in.partake.model.fixture.impl.CommentTestDataProvider;
import in.partake.model.fixture.impl.EnrollmentTestDataProvider;
import in.partake.model.fixture.impl.EventActivityTestDataProvider;
import in.partake.model.fixture.impl.EventFeedTestDataProvider;
import in.partake.model.fixture.impl.EventMessageTestDataProvider;
import in.partake.model.fixture.impl.EventNotificationTestDataProvider;
import in.partake.model.fixture.impl.EventReminderTestDataProvider;
import in.partake.model.fixture.impl.EventTestDataProvider;
import in.partake.model.fixture.impl.EventTicketTestDataProvider;
import in.partake.model.fixture.impl.ImageTestDataProvider;
import in.partake.model.fixture.impl.MessageEnvelopeTestDataProvider;
import in.partake.model.fixture.impl.MessageTestDataProvider;
import in.partake.model.fixture.impl.OpenIDLinkageTestDataProvider;
import in.partake.model.fixture.impl.ThumbnailTestDataProvider;
import in.partake.model.fixture.impl.TwitterLinkageTestDataProvider;
import in.partake.model.fixture.impl.TwitterMessageTestDataProvider;
import in.partake.model.fixture.impl.UserNotificationTestDataProvider;
import in.partake.model.fixture.impl.UserPreferenceTestDataProvider;
import in.partake.model.fixture.impl.UserReceivedMessageTestDataProvider;
import in.partake.model.fixture.impl.UserSentMessageTestDataProvider;
import in.partake.model.fixture.impl.UserTestDataProvider;

import java.util.ArrayList;

/**
 * A set of test data providers.
 * @author shinyak
 *
 */
public class PartakeTestDataProviderSet {
    private ArrayList<TestDataProvider<?>> providers;

    private CalendarLinkageTestDataProvider calendarDataProvider;
    private CommentTestDataProvider commentDataprovider;
    private EnrollmentTestDataProvider enrollmentProvider;
    private EventTestDataProvider eventProvider;
    private EventReminderTestDataProvider eventReminderProvider;
    private ImageTestDataProvider imageProvider;
    private OpenIDLinkageTestDataProvider openIDLinkageProvider;
    private TwitterLinkageTestDataProvider twitterLinkageProvider;
    private UserTestDataProvider userProvider;
    private UserReceivedMessageTestDataProvider userMessageProvider;
    private UserPreferenceTestDataProvider userPreferenceProvider;
    private UserSentMessageTestDataProvider userSentMessageProvider;
    private UserNotificationTestDataProvider userNotificationProvider;
    private EventActivityTestDataProvider eventActivityProvider;
    private EventFeedTestDataProvider eventFeedProvider;
    private EventMessageTestDataProvider eventMessageProvider;
    private EventNotificationTestDataProvider eventNotificationProvider;
    private EventTicketTestDataProvider eventTicketProvider;
    private MessageTestDataProvider messageProvider;
    private MessageEnvelopeTestDataProvider messageEnvelopeProvider;
    private ThumbnailTestDataProvider thumbnailProvider;
    private TwitterMessageTestDataProvider twitterMessageProvider;

    public PartakeTestDataProviderSet() {
        this.providers = new ArrayList<TestDataProvider<?>>();

        providers.add(calendarDataProvider = createCalendarLinkageTestDataProvider());
        providers.add(commentDataprovider = createCommentTestDataProvider());
        providers.add(enrollmentProvider = createEnrollmentTestDataProvider());
        providers.add(eventProvider = createEventTestDataProvider());
        providers.add(eventReminderProvider = createEventReminderTestDataProvider());
        providers.add(imageProvider = createImageTestDataProvider());
        providers.add(openIDLinkageProvider = createOpenIDLinkageTestDataProvider());
        providers.add(twitterLinkageProvider = createTwitterLinkageTestDataProvider());
        providers.add(userProvider = createUserTestDataProvider());
        providers.add(userPreferenceProvider = createUserPreferenceTestDataProvider());
        providers.add(eventActivityProvider = createEventActivityTestDataProvider());
        providers.add(eventFeedProvider = createEventFeedTestDataProvider());
        providers.add(eventMessageProvider = createEventMessageTestDataProvider());
        providers.add(eventNotificationProvider = createEventNotificationTestDataProvider());
        providers.add(eventTicketProvider = createEventTicketTestDataProvider());
        providers.add(messageProvider = createMessageTestDataProvider());
        providers.add(messageEnvelopeProvider = createMessageEnvelopeTestDataProvider());
        providers.add(thumbnailProvider = createThumbnailTestDataProvider());
        providers.add(twitterMessageProvider = createTwitterMessageTestDataProvider());
        providers.add(userMessageProvider = createUserReceivedMessageTestDataProvider());
        providers.add(userSentMessageProvider = createUserSentMessageTestDataProvider());
        providers.add(userNotificationProvider = createUserNotificationTestDataProvider());
    }

    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        for (TestDataProvider<?> provider : providers) {
            provider.createFixtures(con, daos);
        }
    }

    public CalendarLinkageTestDataProvider getCalendarTestDataProvider() {
        return calendarDataProvider;
    }

    public CommentTestDataProvider getCommentDataProvider() {
        return commentDataprovider;
    }

    public EnrollmentTestDataProvider getEnrollmentProvider() {
        return enrollmentProvider;
    }

    public EventTestDataProvider getEventProvider() {
        return eventProvider;
    }

    public EventReminderTestDataProvider getEventReminderProvider() {
        return eventReminderProvider;
    }

    public EventTicketTestDataProvider getEventTicketProvider() {
        return eventTicketProvider;
    }

    public ImageTestDataProvider getImageProvider() {
        return imageProvider;
    }

    public OpenIDLinkageTestDataProvider getOpenIDLinkageProvider() {
        return openIDLinkageProvider;
    }

    public TwitterLinkageTestDataProvider getTwitterLinkageProvider() {
        return twitterLinkageProvider;
    }

    public UserTestDataProvider getUserProvider() {
        return userProvider;
    }

    public UserPreferenceTestDataProvider getUserPreferenceProvider() {
        return userPreferenceProvider;
    }

    public EventActivityTestDataProvider getEventActivityProvider() {
        return eventActivityProvider;
    }

    public EventFeedTestDataProvider getEventFeedProvider() {
        return eventFeedProvider;
    }

    public EventMessageTestDataProvider getEventMessageProvider() {
        return eventMessageProvider;
    }

    public EventNotificationTestDataProvider getEventNotificationProvider() {
        return eventNotificationProvider;
    }

    public MessageTestDataProvider getMessageProvider() {
        return messageProvider;
    }

    public MessageEnvelopeTestDataProvider getMessageEnvelopeProvider() {
        return messageEnvelopeProvider;
    }

    public ThumbnailTestDataProvider getThumbnailProvider() {
        return thumbnailProvider;
    }

    public TwitterMessageTestDataProvider getTwitterMessageProvider() {
        return twitterMessageProvider;
    }

    public UserReceivedMessageTestDataProvider getUserReceivedMessageProvider() {
        return userMessageProvider;
    }

    public UserSentMessageTestDataProvider getUserSentMessageProvider() {
        return userSentMessageProvider;
    }

    public UserNotificationTestDataProvider getUserNotificationProvider() {
        return userNotificationProvider;
    }

    private CalendarLinkageTestDataProvider createCalendarLinkageTestDataProvider() {
        return new CalendarLinkageTestDataProvider();
    }

    private CommentTestDataProvider createCommentTestDataProvider() {
        return new CommentTestDataProvider();
    }

    private EnrollmentTestDataProvider createEnrollmentTestDataProvider() {
        return new EnrollmentTestDataProvider();
    }

    private EventTestDataProvider createEventTestDataProvider() {
        return new EventTestDataProvider();
    }

    private EventReminderTestDataProvider createEventReminderTestDataProvider() {
        return new EventReminderTestDataProvider();
    }

    private ImageTestDataProvider createImageTestDataProvider() {
        return new ImageTestDataProvider();
    }

    private OpenIDLinkageTestDataProvider createOpenIDLinkageTestDataProvider() {
        return new OpenIDLinkageTestDataProvider();
    }

    private TwitterLinkageTestDataProvider createTwitterLinkageTestDataProvider() {
        return new TwitterLinkageTestDataProvider();
    }

    private UserTestDataProvider createUserTestDataProvider() {
        return new UserTestDataProvider();
    }

    private UserPreferenceTestDataProvider createUserPreferenceTestDataProvider() {
        return new UserPreferenceTestDataProvider();
    }

    private EventActivityTestDataProvider createEventActivityTestDataProvider() {
        return new EventActivityTestDataProvider();
    }

    private EventFeedTestDataProvider createEventFeedTestDataProvider() {
        return new EventFeedTestDataProvider();
    }

    private EventMessageTestDataProvider createEventMessageTestDataProvider() {
        return new EventMessageTestDataProvider();
    }

    private EventNotificationTestDataProvider createEventNotificationTestDataProvider() {
        return new EventNotificationTestDataProvider();
    }

    private MessageTestDataProvider createMessageTestDataProvider() {
        return new MessageTestDataProvider();
    }

    private MessageEnvelopeTestDataProvider createMessageEnvelopeTestDataProvider() {
        return new MessageEnvelopeTestDataProvider();
    }

    private TwitterMessageTestDataProvider createTwitterMessageTestDataProvider() {
        return new TwitterMessageTestDataProvider();
    }

    private ThumbnailTestDataProvider createThumbnailTestDataProvider() {
        return new ThumbnailTestDataProvider();
    }

    private UserReceivedMessageTestDataProvider createUserReceivedMessageTestDataProvider() {
        return new UserReceivedMessageTestDataProvider();
    }

    private UserSentMessageTestDataProvider createUserSentMessageTestDataProvider() {
        return new UserSentMessageTestDataProvider();
    }

    private UserNotificationTestDataProvider createUserNotificationTestDataProvider() {
        return new UserNotificationTestDataProvider();
    }

    private EventTicketTestDataProvider createEventTicketTestDataProvider() {
        return new EventTicketTestDataProvider();
    }
}
