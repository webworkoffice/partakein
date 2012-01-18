package in.partake.service;

import in.partake.model.dto.Event;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.model.dto.UserPreference;
import in.partake.resource.PartakeProperties;
import in.partake.util.PDate;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import org.junit.BeforeClass;

public abstract class AbstractServiceTestCaseBase {

    @BeforeClass
    public static void setUpOnce() {
        PartakeProperties.get().reset("mock");
        reset();
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
        Date now = new Date();
        Date createdAt = now;
        Date beginDate = now;

        Event event = new Event("shortId", "title", "summary", "category", null, beginDate, null, 0, "url", "place", "address", "description", "hashTag", "ownerId", null, true, "passcode", false, false, createdAt, null);
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
