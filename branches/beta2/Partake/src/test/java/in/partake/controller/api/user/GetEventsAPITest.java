package in.partake.controller.api.user;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Ignore;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class GetEventsAPITest extends APIControllerTest {
    @Test
    @Ignore("Not implemented yet")
    public void testToGetEventsWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/events");
        addParameter(proxy, "userId", TestDataProvider.DEFAULT_USER_ID);

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultOK(proxy);

        throw new RuntimeException("Not implemented yet");
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToGetEventsWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/events");
        addParameter(proxy, "userId", TestDataProvider.DEFAULT_USER_ID);

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultOK(proxy);

        // NOTE: the same data should be available if not logged in.

        throw new RuntimeException("Not implemented yet");
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToGetInvalidUserEvent() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/events");
        addParameter(proxy, "userId", TestDataProvider.INVALID_USER_ID);

        proxy.execute();
        assertResultInvalid(proxy);
    }
}
