package in.partake.service.mock;

import junit.framework.Assert;
import in.partake.model.EventEx;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.ITwitterLinkageAccess;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.mock.MockConnection;
import in.partake.model.dao.mock.MockDaoFactory;
import in.partake.model.dto.Event;
import in.partake.service.EventService;
import in.partake.service.PartakeService;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class EventServiceTest extends MockServiceTestBase {
    
    @Before
    public void setup() throws Exception {
        // reset all mocks first.
        ((MockDaoFactory) PartakeService.getFactory()).resetAll();
        // then, create fixtures.
        createFixtures();
    }
    
    private void createFixtures() throws Exception {
        IEventAccess eventAccess = PartakeService.getFactory().getEventAccess();
        IUserAccess userAccess = PartakeService.getFactory().getUserAccess();
        ITwitterLinkageAccess twitterAccess = PartakeService.getFactory().getTwitterLinkageAccess();
        
        when(eventAccess.getEventById(any(MockConnection.class), eq("event1"))).thenReturn(createEvent("event1"));
        when(userAccess.getUserById(any(MockConnection.class), eq("ownerId"))).thenReturn(createUser("ownerId"));
        when(twitterAccess.getTwitterLinkageById(any(MockConnection.class), eq(-1))).thenReturn(createTwitterLinkage(-1, "ownerId"));
    }
    
    @Test
    public void testToGetEventById() throws Exception {
        Event event = EventService.get().getEventById("event1");
        Assert.assertEquals("event1", event.getId());
        
        assureAllConnectionsAreReleased();
    }
    
    @Test
    public void testToGetEventExById() throws Exception {
        EventEx event = EventService.get().getEventExById("event1");
        Assert.assertEquals("event1", event.getId());
        Assert.assertNotNull(event.getOwner());
        Assert.assertEquals("ownerId", event.getOwner().getId());
        Assert.assertEquals("accessToken", event.getOwner().getTwitterLinkage().getAccessToken());
        
        assureAllConnectionsAreReleased();
    }
}
