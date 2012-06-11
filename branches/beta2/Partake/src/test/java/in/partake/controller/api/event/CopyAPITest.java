package in.partake.controller.api.event;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.resource.UserErrorCode;

import java.util.List;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class CopyAPITest extends APIControllerTest {

    @Test
    public void testToCreate() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/copy");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);

        proxy.execute();
        assertResultOK(proxy);

        String eventId = getJSON(proxy).getString("eventId");

        Event originalEvent = loadEvent(DEFAULT_EVENT_ID);
        List<EventTicket> originalTickets = loadEventTickets(DEFAULT_EVENT_ID);

        Event event = loadEvent(eventId);
        List<EventTicket> tickets = loadEventTickets(eventId);

        assertThat(event.getId(), is(not(originalEvent.getId())));
        assertThat(event.getOwnerId(), is(EVENT_OWNER_ID));
        assertThat(event.getSummary(), is(originalEvent.getSummary()));
        assertThat(event.isDraft(), is(true));

        assertThat(tickets.size(), is(1));
        assertThat(tickets.get(0).getName(), is(originalTickets.get(0).getName()));
        assertThat(tickets.get(0).getId(), is(not(originalTickets.get(0).getId())));
    }

    @Test
    public void testToCreateByEditor() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/copy");
        loginAs(proxy, EVENT_EDITOR_ID);
        addValidSessionTokenToParameter(proxy);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);

        proxy.execute();
        assertResultOK(proxy);

        String eventId = getJSON(proxy).getString("eventId");

        Event event = loadEvent(eventId);

        assertThat(event.getOwnerId(), is(EVENT_EDITOR_ID));
    }

    @Test
    public void testToCreateByNonRelatedUser() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/copy");
        loginAs(proxy, EVENT_UNRELATED_USER_ID);
        addValidSessionTokenToParameter(proxy);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testToCreateWithoutEventId() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/copy");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_EVENT_ID);
    }

    @Test
    public void testToCreateWithInvalidEventId() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/copy");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addParameter(proxy, "eventId", INVALID_EVENT_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_EVENT_ID);
    }

    @Test
    public void testToCreateWithoutSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/copy");
        loginAs(proxy, EVENT_OWNER_ID);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

    @Test
    public void testToCreateWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/copy");
        loginAs(proxy, EVENT_OWNER_ID);
        addInvalidSessionTokenToParameter(proxy);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }
}
