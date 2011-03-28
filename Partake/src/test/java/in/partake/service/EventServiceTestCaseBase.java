package in.partake.service;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.BinaryData;
import in.partake.model.dto.Event;
import in.partake.model.dto.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;

public abstract class EventServiceTestCaseBase extends AbstractServiceTestCaseBase {
    private final EventService service = EventService.get();

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
    public void testToStoreAndDeleteImages() throws DAOException {
        final User owner = createUser(createRandomId());
        final Event event = createEvent("this id will be overwritten.");
        event.setOwnerId(owner.getId());
        BinaryData foreImageEmbryo = new BinaryData("text", bytes("foreImage"));
        BinaryData backImageEmbryo = new BinaryData("text", bytes("backImage"));
        String eventId = service.create(event,foreImageEmbryo, backImageEmbryo);
        {
            Event storedEvent = service.getEventById(eventId);
            Assert.assertEquals(storedEvent, event);
            String foreImageId = storedEvent.getForeImageId();
            Assert.assertNotNull(foreImageId);
            Assert.assertEquals("foreImage", new String(service.getBinaryData(foreImageId).getData()));
            String backImageId = storedEvent.getBackImageId();
            Assert.assertNotNull(backImageId);
            Assert.assertEquals("backImage", new String(service.getBinaryData(backImageId).getData()));
        }

        service.update(event, event, true, null, true, null);
        {
            Event storedEvent = service.getEventById(eventId);
            Assert.assertEquals(storedEvent, event);
            Assert.assertNull(storedEvent.getForeImageId());
            Assert.assertNull(storedEvent.getBackImageId());
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
}
