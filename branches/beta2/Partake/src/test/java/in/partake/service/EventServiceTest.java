package in.partake.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.dto.BinaryData;
import in.partake.model.dto.Event;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.fixture.TestDataProvider;
import in.partake.model.fixture.impl.UserTestDataProvider;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.interceptor.annotations.Before;

public class EventServiceTest extends AbstractServiceTestCaseBase {
    private static final Charset UTF8 = Charset.forName("utf-8");
    private final DeprecatedEventDAOFacade service = DeprecatedEventDAOFacade.get();

    @Before
    public void setUp() throws DAOException {
        TestDatabaseService.setDefaultFixtures();
    }
    
    @Test
    public void testToGetEventById() throws Exception {
        String eventId = createRandomId();
        Assert.assertNull(service.getEventById(eventId));

        Event event = createEvent(eventId);
        String updatedEventId = service.create(event, null, null);
        Assert.assertFalse(updatedEventId.equals(eventId));

        Event storedEvent = service.getEventById(updatedEventId);
        Assert.assertNotNull(storedEvent);
        Assert.assertEquals(updatedEventId, storedEvent.getId());
    }

    @Test
    public void testToCountEvents() throws DAOException {
        int eventCountsAtFirst = service.countEvents().numEvent;
        int eventsToAdd = 10;

        for (int i = 1; i <= eventsToAdd; ++i) {
            Event event = createEvent("this id will be overwritten.");
            event.setPrivate(false);
            service.create(event, null, null);
            Assert.assertEquals(eventCountsAtFirst + i, service.countEvents().numEvent);
        }
        for (int i = 1; i <= eventsToAdd; ++i) {
            Event event = createEvent("this id will be overwritten.");
            event.setPrivate(true);
            service.create(event, null, null);
            Assert.assertEquals(eventCountsAtFirst + eventsToAdd + i, service.countEvents().numEvent);
        }
    }


    @Test
    public void testToCountPrivateEvents() throws DAOException {
        int eventCountsAtFirst = service.countEvents().numPrivateEvent;
        int eventsToAdd = 10;
        for (int i = 1; i <= eventsToAdd; ++i) {
            Event event = createEvent("this id will be overwritten.");
            event.setPrivate(false);
            service.create(event, null, null);
            Assert.assertEquals(eventCountsAtFirst, service.countEvents().numPrivateEvent);
        }
        for (int i = 1; i <= eventsToAdd; ++i) {
            Event event = createEvent("this id will be overwritten.");
            event.setPrivate(true);
            service.create(event, null, null);
            Assert.assertEquals(eventCountsAtFirst + i, service.countEvents().numPrivateEvent);
        }
    }

    @Test
    public void testToRemoveAndIterate() throws DAOException {
        final User owner = createUser(createRandomId());
        final int events = 5;
        final List<String> eventIds = new ArrayList<String>(events);

        for (int i = 0; i < events; ++i) {
            final Event eventEmbryo = createEvent("this id will be overwritten.");
            eventEmbryo.setOwnerId(owner.getId());
            eventIds.add(service.create(eventEmbryo, null, null));
        }

        List<Event> services = getAndFilterEventsOwnedBy(owner, eventIds);
        Assert.assertEquals(events, services.size());

        while (!eventIds.isEmpty()) {
            final String removedId = eventIds.get(eventIds.size() / 2);
            service.remove(removedId);
            eventIds.remove(removedId);

            services = getAndFilterEventsOwnedBy(owner, eventIds);
            Assert.assertEquals(eventIds.size(), services.size());
        }
    }

    @Test
    public void testStoredEventEqualsSourceEvent() throws DAOException {
        final User owner = createUser(createRandomId());
        final Event source = createEvent("this id will be overwritten.");
        source.setOwnerId(owner.getId());
        {
            String eventId = service.create(source, null, null);
            Event storedEvent = service.getEventById(eventId);
            // TODO PostgresのTimestampの精度上、equals()が成立しない可能性が高い
            Assert.assertEquals(source, storedEvent);
        }
        final BinaryData foreImageEmbryo = new BinaryData("text", "foreImage".getBytes(UTF8));
        final BinaryData backImageEmbryo = new BinaryData("text", "backImage".getBytes(UTF8));
        {
            String eventId = service.create(source, foreImageEmbryo, backImageEmbryo);
            Event storedEvent = service.getEventById(eventId);
            Assert.assertEquals(source, storedEvent);
        }
    }

    @Test
    public void testToGetRecentEvents() throws DAOException {
        String eventTitle = Long.toString(System.currentTimeMillis());
        long[] timestamps = new long[] {1000000L, 0L, 2000000L};
        for (long timestamp : timestamps) {
            Event event = createEvent("this id will be overwritten.");
            event.setCreatedAt(new Date(timestamp));
            event.setTitle(eventTitle);
            event.setPrivate(false);
            event.setDeadline(new Date(System.currentTimeMillis() + 60 * 1000));	// 締切りを過ぎたイベントは表示されないので大きく設定する
            service.create(event, null, null);
        }

        List<Event> events = service.getRecentEvents(100);	// ゴミが多いとこれでも足りないかも
        int count = 0;
        Date date = null;
        for (Event event : events) {
            if (event == null || !eventTitle.equals(event.getTitle())) { continue; }
            ++count;
            if (date != null) {
                Assert.assertTrue(event.getCreatedAt().before(date));
            }
            date = event.getCreatedAt();
        }
        Assert.assertEquals(timestamps.length, count);
    }

    @Test
    public void testToGetUpcomingEvents() throws DAOException {
        String eventTitle = Long.toString(System.currentTimeMillis());
        long[] timestamps = new long[] {1000000L, 0L, 2000000L};
        for (long timestamp : timestamps) {
            Event event = createEvent("this id will be overwritten.");
            event.setBeginDate(new Date(timestamp));
            event.setTitle(eventTitle);
            event.setPrivate(false);
            event.setDeadline(new Date(System.currentTimeMillis() + 60 * 1000));	// 締切りを過ぎたイベントは表示されないので大きく設定する
            service.create(event, null, null);
        }

        List<Event> events = service.getUpcomingEvents(100, "all");	// ゴミが多いとこれでも足りないかも
        int count = 0;
        Date date = null;
        for (Event event : events) {
            if (event == null || !eventTitle.equals(event.getTitle())) { continue; }
            ++count;
            if (date != null) {
                Assert.assertTrue(event.getBeginDate().after(date));
            }
            date = event.getBeginDate();
        }
        Assert.assertEquals(timestamps.length, count);
    }

    @Test
    public void testThatFinishedEventsIsNotFoundByGetRecentEvents() throws DAOException {
        String eventTitle = Long.toString(System.currentTimeMillis());
        long[] timestamps = new long[] {1000000L, 0L, 2000000L};
        for (long timestamp : timestamps) {
            Event event = createEvent("this id will be overwritten.");
            event.setBeginDate(new Date(timestamp));
            event.setTitle(eventTitle);
            event.setPrivate(false);
            event.setDeadline(new Date(0L));	// 締切りを過ぎたイベントは表示されない、ということを確認するためにゼロを入れる
            service.create(event, null, null);
        }

        List<Event> events = service.getRecentEvents(100);	// ゴミが多いとこれでも足りないかも
        for (Event event : events) {
            if (event == null || !eventTitle.equals(event.getTitle())) { continue; }
            Assert.fail();
        }
    }

    @Test
    public void testThatFinishedEventsIsNotFoundByGetUpcomingEvents() throws DAOException {
        String eventTitle = Long.toString(System.currentTimeMillis());
        long[] timestamps = new long[] {1000000L, 0L, 2000000L};
        for (long timestamp : timestamps) {
            Event event = createEvent("this id will be overwritten.");
            event.setBeginDate(new Date(timestamp));
            event.setTitle(eventTitle);
            event.setPrivate(false);
            event.setDeadline(new Date(0L));	// 締切りを過ぎたイベントは表示されない、ということを確認するためにゼロを入れる
            service.create(event, null, null);
        }

        List<Event> events = service.getUpcomingEvents(100, "all");	// ゴミが多いとこれでも足りないかも
        for (Event event : events) {
            if (event == null || !eventTitle.equals(event.getTitle())) { continue; }
            Assert.fail();
        }
    }

    private List<Event> getAndFilterEventsOwnedBy(User owner,
            final List<String> eventIds) throws DAOException {
        final List<Event> services = service.getEventsOwnedBy(owner);
        for (Iterator<Event> iter = services.iterator(); iter.hasNext();) {
            Event event = iter.next();
            Assert.assertEquals(owner.getId(), event.getOwnerId());
            if (!eventIds.contains(event.getId())) {
                iter.remove();
            }
        }
        return services;
    }

	@Test
	public void testToFindCanceledEvent() throws DAOException {
		String userId = UserTestDataProvider.DEFAULT_USER_ID;
		String eventId = TestDataProvider.DEFAULT_EVENT_ID;
		assertThat(service.getEventById(eventId), is(not(nullValue())));

		service.enroll(userId, eventId, ParticipationStatus.ENROLLED, "", false, false);
		int enrolledEvents = service.getUnfinishedEnrolledEvents(userId).size();
		service.enroll(userId, eventId, ParticipationStatus.CANCELLED, "", false, false);
		assertThat(service.getUnfinishedEnrolledEvents(userId).size(), is(enrolledEvents - 1));
		service.enroll(userId, eventId, ParticipationStatus.RESERVED, "", false, false);
		assertThat(service.getUnfinishedEnrolledEvents(userId).size(), is(enrolledEvents));
	}

	/**
	 * @see http://code.google.com/p/partakein/issues/detail?id=204
	 */
	@Test
	public void testToCancelToUseDeadline() throws DAOException {
		final User owner = createUser(createRandomId());
		final Event source = createEvent("this id will be overwritten.");
		final Date deadline = new Date();
		source.setOwnerId(owner.getId());
		source.setDeadline(deadline);
		{
			String eventId = service.create(source, null, null);
			Event storedEvent = service.getEventById(eventId);
			assertThat(storedEvent.getDeadline(), is(not(nullValue())));
		}
		Event updated = source.copy();
		updated.setDeadline(null);
		{
			service.update(source, updated, false, null, false, null);
			Event storedEvent = service.getEventById(updated.getId());
			assertThat(storedEvent.getDeadline(), is(nullValue()));
		}
	}

	/**
	 * @see http://code.google.com/p/partakein/issues/detail?id=204
	 */
	@Test
	public void testToCancelToUseEndDate() throws DAOException {
		final User owner = createUser(createRandomId());
		final Event source = createEvent("this id will be overwritten.");
		final Date endDate = new Date();
		source.setOwnerId(owner.getId());
		source.setEndDate(endDate);
		{
			String eventId = service.create(source, null, null);
			Event storedEvent = service.getEventById(eventId);
			assertThat(storedEvent.getEndDate(), is(not(nullValue())));
		}
		Event updated = source.copy();
		updated.setEndDate(null);
		{
			service.update(source, updated, false, null, false, null);
			Event storedEvent = service.getEventById(updated.getId());
			assertThat(storedEvent.getEndDate(), is(nullValue()));
		}
	}
}
