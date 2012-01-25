package in.partake.service;

import in.partake.model.dto.Event;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.model.dto.UserPreference;
import in.partake.model.fixture.impl.EventTestDataProvider;
import in.partake.resource.PartakeProperties;
import in.partake.util.PDate;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.BeforeClass;

public abstract class AbstractServiceTestCaseBase {

    @BeforeClass
    public static void setUpOnce() {
        // TODO: Should share the code with AbstractConnectionTestCaseBase.
        PartakeProperties.get().reset("unittest");
        
        try {
            if (PartakeProperties.get().getBoolean("in.partake.database.unittest_initialization"))
                initializeDataSource();
        } catch (NameAlreadyBoundException e) {
            // Maybe already DataSource is created.
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        
        TestService.initialize();
    }
    
    private static void initializeDataSource() throws NamingException {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");

        InitialContext ic = new InitialContext();
        ic.createSubcontext("java:");
        ic.createSubcontext("java:/comp");
        ic.createSubcontext("java:/comp/env");
        ic.createSubcontext("java:/comp/env/jdbc");

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(PartakeProperties.get().getString("comp.env.jdbc.postgres.driver"));
        ds.setUrl(PartakeProperties.get().getString("comp.env.jdbc.postgres.url"));
        ds.setUsername(PartakeProperties.get().getString("comp.env.jdbc.postgres.user"));
        ds.setPassword(PartakeProperties.get().getString("comp.env.jdbc.postgres.password"));

        ic.bind("java:/comp/env/jdbc/postgres", ds);
    }


    /**
     * call this method and {@link PartakeProperties#reset(String)} at EachTestCase#setUpOnce() which is annotated as @BeforeClass.
     */
    protected static void reset() {
        PDate.resetCurrentDate();
        PartakeService.initialize();
    }

    protected final String createRandomId() {
        return UUID.randomUUID().toString();
    }

    /**
     * utility function to create an event.
     * @param id
     * @return
     */
    protected Event createEvent(String id) {
        EventTestDataProvider provider = TestService.get().testDataProviderSet.getEventProvider();
        Event event = provider.create();
        
        event.setId(id);
        return event;
    }

    /**
     * utility function to create an event.
     * @param id
     * @param beginYear
     * @param beginMonth
     * @param beginDay
     * @param beginHour
     * @param beginMin
     * @return
     */
    protected Event createEvent(String id, int beginYear, int beginMonth, int beginDay, int beginHour, int beginMin) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("JST"), Locale.JAPANESE);
        Date createdAt = calendar.getTime();

        calendar.clear();
        calendar.set(Calendar.YEAR, beginYear);
        calendar.set(Calendar.MONTH, beginMonth - 1);
        calendar.set(Calendar.DAY_OF_MONTH, beginDay);
        calendar.set(Calendar.HOUR_OF_DAY, beginHour);
        calendar.set(Calendar.MINUTE, beginMin);
        Date beginDate = calendar.getTime();

        Event event = new Event("shortId", "title", "summary", "category", null, beginDate, null, 0, "url", "place", "address", "description", "hashTag", "ownerId", null, true, "passcode", false, false, createdAt, null);
        event.setId(id);
        return event;
    }

    protected User createUser(String userId) {
        User user = new User(userId, -1, new Date(), null);
        return user;
    }

    protected TwitterLinkage createTwitterLinkage(int twitterId, String userId) {
        TwitterLinkage linkage = new TwitterLinkage(-1, "screenName", "name", "accessToken", "accessTokenSecret", "http://example.com/profile.image.jpg", userId);
        return linkage;
    }

    protected UserPreference createUserPreference(String userId) {
        UserPreference preference = new UserPreference();
        preference.setUserId(userId);
        return preference;
    }
}
