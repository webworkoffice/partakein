package in.partake.model.fixture;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.fixture.impl.BinaryTestDataProvider;
import in.partake.model.fixture.impl.CalendarLinkageTestDataProvider;
import in.partake.model.fixture.impl.CommentTestDataProvider;
import in.partake.model.fixture.impl.EnrollmentTestDataProvider;
import in.partake.model.fixture.impl.EnvelopeTestDataProvider;
import in.partake.model.fixture.impl.EventReminderTestDataProvider;
import in.partake.model.fixture.impl.EventTestDataProvider;
import in.partake.model.fixture.impl.ImageTestDataProvider;
import in.partake.model.fixture.impl.OpenIDLinkageTestDataProvider;
import in.partake.model.fixture.impl.TwitterLinkageTestDataProvider;
import in.partake.model.fixture.impl.UserPreferenceTestDataProvider;
import in.partake.model.fixture.impl.UserTestDataProvider;

import java.util.ArrayList;

/**
 * A set of test data providers.
 * @author shinyak
 *
 */
public class PartakeTestDataProviderSet {
    private ArrayList<TestDataProvider<?>> providers;

    private BinaryTestDataProvider binaryDataProvider;
    private CalendarLinkageTestDataProvider calendarDataProvider;
    private CommentTestDataProvider commentDataprovider;
    private EnrollmentTestDataProvider enrollmentProvider;
    private EnvelopeTestDataProvider envelopeTestDataProvider;
    private EventTestDataProvider eventProvider;
    private EventReminderTestDataProvider eventReminderProvider;
    private ImageTestDataProvider imageProvider;
    private OpenIDLinkageTestDataProvider openIDLinkageProvider;
    private TwitterLinkageTestDataProvider twitterLinkageProvider;
    private UserTestDataProvider userProvider;
    private UserPreferenceTestDataProvider userPreferenceProvider;

    public PartakeTestDataProviderSet() {
        this.providers = new ArrayList<TestDataProvider<?>>();

        providers.add(binaryDataProvider = createBinaryTestDataProvider());
        providers.add(calendarDataProvider = createCalendarLinkageTestDataProvider());
        providers.add(commentDataprovider = createCommentTestDataProvider());
        providers.add(enrollmentProvider = createEnrollmentTestDataProvider());
        providers.add(envelopeTestDataProvider = createEnvelopeTestDataProvider());
        providers.add(eventProvider = createEventTestDataProvider());
        providers.add(eventReminderProvider = createEventReminderTestDataProvider());
        providers.add(imageProvider = createImageTestDataProvider());
        providers.add(openIDLinkageProvider = createOpenIDLinkageTestDataProvider());
        providers.add(twitterLinkageProvider = createTwitterLinkageTestDataProvider());
        providers.add(userProvider = createUserTestDataProvider());
        providers.add(userPreferenceProvider = createUserPreferenceTestDataProvider());
    }

    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        for (TestDataProvider<?> provider : providers) {
            provider.createFixtures(con, daos);
        }
    }

    public BinaryTestDataProvider getBinaryTestDataProvider() {
        return binaryDataProvider;
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

    public EnvelopeTestDataProvider getEnvelopeTestDataProvider() {
        return envelopeTestDataProvider;
    }

    public EventTestDataProvider getEventProvider() {
        return eventProvider;
    }

    public EventReminderTestDataProvider getEventReminderProvider() {
        return eventReminderProvider;
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

    private BinaryTestDataProvider createBinaryTestDataProvider() {
        return new BinaryTestDataProvider();
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

    private EnvelopeTestDataProvider createEnvelopeTestDataProvider() {
        return new EnvelopeTestDataProvider();
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
}
