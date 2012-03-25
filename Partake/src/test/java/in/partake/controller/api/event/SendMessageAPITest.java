package in.partake.controller.api.event;

import in.partake.base.Util;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class SendMessageAPITest extends APIControllerTest {
    @Test
    public void testToSendMessageForOwnedEvent() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/sendMessage");

        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "message", "hogehogehoge");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        // TODO: Check DB.
    }

    @Test
    public void testToSendLongMessage() throws Exception {
        String longMessage = Util.randomString(1024);

        ActionProxy proxy = getActionProxy("/api/event/sendMessage");

        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "message", longMessage);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testToSendMessageForManagedEvent() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/sendMessage");

        loginAs(proxy, TestDataProvider.EVENT_EDITOR_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "message", "hogehogehoge");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testToSendMessageForNotOwnedEvent() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/sendMessage");

        loginAs(proxy, TestDataProvider.EVENT_UNRELATED_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "message", "hogehogehoge");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testToSendMessageWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/sendMessage");

        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "message", "hogehogehoge");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }
}
