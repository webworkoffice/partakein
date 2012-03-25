package in.partake.controller.api.event;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;

public class GetEventAPITest extends APIControllerTest {
    @Test
    public void testGetEvent() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testGetEventWithoutId() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/");
        // addParameter(proxy, "eventId", TestDataProvider.EVENT_ID1);

        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testGetEventWithInvalidId() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", "invalid-event-id");

        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testGetPrivateEventByOwner() throws Exception {
        // If a private event is requested without login,
        // 'forbidden' should be returned.
        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", TestDataProvider.PRIVATE_EVENT_ID);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testGetPrivateEventWithoutLogin() throws Exception {
        // If a private event is requested without login,
        // 'forbidden' should be returned.
        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", TestDataProvider.PRIVATE_EVENT_ID);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testGetPrivateEventWithInvalidLogin() throws Exception {
        // If a private event is requested with invalid user,
        // 'forbidden' should be returned.
        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", TestDataProvider.PRIVATE_EVENT_ID);
        loginAs(proxy, TestDataProvider.EVENT_UNRELATED_USER_ID);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testGetPrivateEventWithCorrectPasscode() throws Exception {
        // Event if not logged in, when the correct passcode is provided,
        // 'get' API should succeed.

        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", TestDataProvider.PRIVATE_EVENT_ID);
        addParameter(proxy, "passcode", "passcode");

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testGetPrivateEventwithInvalidPasscode() throws Exception {
        // When invalid passcode is provided, 'forbidden' should be returned.
        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", TestDataProvider.PRIVATE_EVENT_ID);
        addParameter(proxy, "passcode", "invalid-passcode");

        proxy.execute();
        assertResultForbidden(proxy);
    }

}
