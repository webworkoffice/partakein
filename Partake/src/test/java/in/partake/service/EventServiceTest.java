package in.partake.service;

import junit.framework.Assert;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.mock.MockConnection;
import in.partake.model.dto.Event;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class EventServiceTest extends ServiceTestBase {
    @Before
    public void setup() throws Exception {
        // TODO: PartakeService を reset するべき。同じ mock object になんども値が入ってしまう。
        createFixtures();
    }
    
    private void createFixtures() throws Exception {
        IEventAccess eventAccess = PartakeService.getFactory().getEventAccess(); 
        when(eventAccess.getEventById(any(MockConnection.class), eq("event1"))).thenReturn(createEvent("event1"));
    }
    
    @Test
    public void testToGetEventById() throws Exception {
        Event event = EventService.get().getEventById("event1");
        Assert.assertEquals("event1", event.getId());
        
        assureAllConnectionsAreReleased();
    }
}
