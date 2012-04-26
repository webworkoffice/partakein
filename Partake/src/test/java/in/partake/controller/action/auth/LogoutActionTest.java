package in.partake.controller.action.auth;

import in.partake.controller.action.ActionControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class LogoutActionTest extends ActionControllerTest {

    @Test
    public void testWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/auth/logout");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertLoggedOut(proxy);
        assertRedirectedTo("/");
   }

    @Test
    public void testWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/auth/logout");

        proxy.execute();
        assertLoggedOut(proxy);
        assertRedirectedTo("/");
    }
}
