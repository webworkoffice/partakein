package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventRelation;
import in.partake.model.fixture.TestDataProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;

import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author skypencil (@eller86)
 */
public final class EventTest extends AbstractPartakeModelTest<Event> {
    @Override
    protected Event copy(Event t) {
        return new Event(t);
    }

    @Override
    protected TestDataProvider<Event> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getEventProvider();
    }

    Event[] samples;

    @Before
    public void createSamples() {
        samples = new Event[] {
                new Event(),
                new Event("id", "shortId", "title", "summary", "category",
                        new Date(), new Date(), new Date(), 0, "url", "place",
                        "address", "description", "#hashTag", "ownerId", null,
                        "foreImageId", "backImageId", true, "passcode", false, false,
                        new ArrayList<EventRelation>(),
                        new Date(), new Date(), -1),
                new Event("id2", "shortId2", "title2", "summary2", "category2",
                        new Date(1), new Date(2), new Date(3), 1, "url2", "place2",
                        "address2", "description2", "#hashTag2", "ownerId2", "hoge,fuga",
                        "foreImageId2", "backImageId2", false, "passcode2", false, false,
                        new ArrayList<EventRelation>(),
                        new Date(4), new Date(5), 1)
        };
    }

    @Test
    public void testCopyConstructor() {
        for (Event source : samples) {
            // Event class doesn't override #equals() method.
            // Assert.assertEquals(source, new Event(source));

            Assert.assertEquals(source.getId(), new Event(source).getId());
            Assert.assertEquals(source.getShortId(), new Event(source).getShortId());
            Assert.assertEquals(source.getTitle(), new Event(source).getTitle());
            Assert.assertEquals(source.getSummary(), new Event(source).getSummary());
            Assert.assertEquals(source.getCategory(), new Event(source).getCategory());
            Assert.assertEquals(source.getDeadline(), new Event(source).getDeadline());
            Assert.assertEquals(source.getBeginDate(), new Event(source).getBeginDate());
            Assert.assertEquals(source.getEndDate(), new Event(source).getEndDate());
            Assert.assertEquals(source.getCapacity(), new Event(source).getCapacity());
            Assert.assertEquals(source.getUrl(), new Event(source).getUrl());
            Assert.assertEquals(source.getPlace(), new Event(source).getPlace());
            Assert.assertEquals(source.getAddress(), new Event(source).getAddress());
            Assert.assertEquals(source.getDescription(), new Event(source).getDescription());
            Assert.assertEquals(source.getHashTag(), new Event(source).getHashTag());
            Assert.assertEquals(source.getOwnerId(), new Event(source).getOwnerId());
            Assert.assertEquals(source.getManagerScreenNames(), new Event(source).getManagerScreenNames());
            Assert.assertEquals(source.getForeImageId(), new Event(source).getForeImageId());
            Assert.assertEquals(source.getBackImageId(), new Event(source).getBackImageId());
            Assert.assertEquals(source.isPrivate(), new Event(source).isPrivate());
            Assert.assertEquals(source.getPasscode(), new Event(source).getPasscode());
            Assert.assertEquals(source.isPreview(), new Event(source).isPreview());
            Assert.assertEquals(source.getCreatedAt(), new Event(source).getCreatedAt());
            Assert.assertEquals(source.getModifiedAt(), new Event(source).getModifiedAt());
            Assert.assertEquals(source.getRevision(), new Event(source).getRevision());

            if (source.getDeadline() != null) {
                Assert.assertNotSame(source.getDeadline(), new Event(source).getDeadline());
            }
            if (source.getBeginDate() != null) {
                Assert.assertNotSame(source.getBeginDate(), new Event(source).getBeginDate());
            }
            if (source.getEndDate() != null) {
                Assert.assertNotSame(source.getEndDate(), new Event(source).getEndDate());
            }
            if (source.getCreatedAt() != null) {
                Assert.assertNotSame(source.getCreatedAt(), new Event(source).getCreatedAt());
            }
            if (source.getModifiedAt() != null) {
                Assert.assertNotSame(source.getModifiedAt(), new Event(source).getModifiedAt());
            }
        }
    }

    @Test
    public void testCopyConstructorByReflection() throws IllegalArgumentException, IllegalAccessException {
        for (Event source : samples) {
            Event copy = new Event(source);

            for (Field field : Event.class.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    Assert.assertEquals(field.get(source), field.get(copy));
                }
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void testCopyConstructorByNullValue() {
        new Event((Event) null);
    }

    @Test
    public void testCopyConstructorByFlozenInstance() {
        Event source = new Event();
        Assert.assertFalse(source.isFrozen());

        source.freeze();
        Assert.assertTrue(source.isFrozen());

        Assert.assertFalse(new Event(source).isFrozen());
    }

    @Test
    public void testIsManager() throws Exception {
        Event event = new Event();
        event.setManagerScreenNames("A, B, C, ABC   ,,,,,    D   ,E,F");

        Assert.assertTrue(event.isManager("A"));
        Assert.assertTrue(event.isManager("B"));
        Assert.assertTrue(event.isManager("C"));
        Assert.assertTrue(event.isManager("ABC"));
        Assert.assertTrue(event.isManager("D"));
        Assert.assertTrue(event.isManager("E"));
        Assert.assertTrue(event.isManager("F"));

        Assert.assertFalse(event.isManager(null));
        Assert.assertFalse(event.isManager(""));
        Assert.assertFalse(event.isManager("G"));
        Assert.assertFalse(event.isManager("a"));
        Assert.assertFalse(event.isManager("hoge"));
    }

    @Test
    public void testIsManagerWhenManagerScreenNamesIsNull() throws Exception {
        Event event = new Event();
        event.setManagerScreenNames(null);

        Assert.assertFalse(event.isManager(null));
        Assert.assertFalse(event.isManager(""));
        Assert.assertFalse(event.isManager("A"));
        Assert.assertFalse(event.isManager("B"));
        Assert.assertFalse(event.isManager("manager"));
    }

    @Test
    public void testIsManagerWhenManagerScreenNameIsEmpty() throws Exception {
        Event event = new Event();
        event.setManagerScreenNames("");

        Assert.assertFalse(event.isManager(null));
        Assert.assertFalse(event.isManager(""));
        Assert.assertFalse(event.isManager("A"));
        Assert.assertFalse(event.isManager("B"));
        Assert.assertFalse(event.isManager("manager"));
    }

    @Test
    public void testIsManagerWhenManagerScreenNameIsBlank() throws Exception {
        Event event = new Event();
        event.setManagerScreenNames("    ");

        Assert.assertFalse(event.isManager(null));
        Assert.assertFalse(event.isManager(""));
        Assert.assertFalse(event.isManager("A"));
        Assert.assertFalse(event.isManager("B"));
        Assert.assertFalse(event.isManager("manager"));
    }

    @Test
    public void testToJsonWhenBeginDateExistsAndEndDateIsNull() {
        Event event = new Event();
        event.setBeginDate(new Date(0L));
        JSONObject json = event.toSafeJSON();
        Assert.assertEquals("1970/01/01 09:00:00", json.getString("beginDate"));
        Assert.assertFalse(json.containsKey("endDate"));
    }
}
