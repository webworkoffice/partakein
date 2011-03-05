package in.partake.service;

import in.partake.model.dto.Event;
import junit.framework.Assert;

import org.junit.Test;

public abstract class EventServiceTestCaseBase extends AbstractServiceTestCaseBase {
    @Test
    public void testToGetEventById() throws Exception {
        EventService service = EventService.get();
        String eventId = createRandomId();
        Assert.assertNull(service.getEventById(eventId));

        Event event = createEvent(eventId);
        String updatedEventId = service.create(event, null, null);
        Assert.assertFalse(updatedEventId.equals(eventId));

        Event storedEvent = service.getEventById(updatedEventId);
        Assert.assertNotNull(storedEvent);
        Assert.assertEquals(updatedEventId, storedEvent.getId());
    }
}
