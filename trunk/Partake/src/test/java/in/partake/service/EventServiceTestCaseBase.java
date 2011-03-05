package in.partake.service;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

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
