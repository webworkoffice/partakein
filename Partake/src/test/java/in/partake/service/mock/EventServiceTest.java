package in.partake.service.mock;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import in.partake.model.EventEx;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.IEventRelationAccess;
import in.partake.model.dao.ITwitterLinkageAccess;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.mock.MockConnection;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.pk.EventRelationPK;
import in.partake.service.EventService;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class EventServiceTest extends MockServiceTestBase {

    @Before
    public void setup() throws Exception {
        // create fixtures.
        reset();
        createFixtures();
    }

    private void createFixtures() throws Exception {
        IEventAccess eventAccess = getFactory().getEventAccess();
        IUserAccess userAccess = getFactory().getUserAccess();
        ITwitterLinkageAccess twitterAccess = getFactory().getTwitterLinkageAccess();

        when(eventAccess.find(any(MockConnection.class), eq("event1"))).thenReturn(createEvent("event1"));
        when(userAccess.find(any(MockConnection.class), eq("ownerId"))).thenReturn(createUser("ownerId"));
        when(twitterAccess.find(any(MockConnection.class), eq("-1"))).thenReturn(createTwitterLinkage(-1, "ownerId"));
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

    @Test
    public void testToSetEventRelations1() throws Exception {
        IEventRelationAccess dao = getFactory().getEventRelationAccess();

        String eventId = "eventId";
        List<EventRelation> relations = new ArrayList<EventRelation>();
        relations.add(new EventRelation("eventId", "dstEventId1", true, true));
        relations.add(new EventRelation("eventId", "dstEventId2", true, true));
        relations.add(new EventRelation("eventId", "dstEventId3", true, true));

        EventService.get().setEventRelations(eventId, relations);

        verify(dao, times(1)).put(any(MockConnection.class), eq(new EventRelation("eventId", "dstEventId1", true, true)));
        verify(dao, times(1)).put(any(MockConnection.class), eq(new EventRelation("eventId", "dstEventId2", true, true)));
        verify(dao, times(1)).put(any(MockConnection.class), eq(new EventRelation("eventId", "dstEventId3", true, true)));
        verify(dao, never()).remove(any(MockConnection.class), any(EventRelationPK.class));
    }

    @Test
    public void testToSetEventRelations2() throws Exception {
        IEventRelationAccess dao = getFactory().getEventRelationAccess();
        {
            List<EventRelation> relations = new ArrayList<EventRelation>();
            relations.add(new EventRelation("eventId", "dstEventId0", false, false));
            when(dao.findByEventId(any(MockConnection.class), eq("eventId"))).thenReturn(relations);
        }

        {
            String eventId = "eventId";
            List<EventRelation> relations = new ArrayList<EventRelation>();
            relations.add(new EventRelation("eventId", "dstEventId1", true, true));
            relations.add(new EventRelation("eventId", "dstEventId2", true, true));
            relations.add(new EventRelation("eventId", "dstEventId3", true, true));

            EventService.get().setEventRelations(eventId, relations);
        }

        verify(dao, times(1)).remove(any(MockConnection.class), eq(new EventRelationPK("eventId", "dstEventId0")));
        verify(dao, times(1)).put(any(MockConnection.class), eq(new EventRelation("eventId", "dstEventId1", true, true)));
        verify(dao, times(1)).put(any(MockConnection.class), eq(new EventRelation("eventId", "dstEventId2", true, true)));
        verify(dao, times(1)).put(any(MockConnection.class), eq(new EventRelation("eventId", "dstEventId3", true, true)));
    }
}
