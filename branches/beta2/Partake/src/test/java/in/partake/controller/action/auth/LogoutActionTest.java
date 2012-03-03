package in.partake.controller.action.auth;

import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class LogoutActionTest extends AbstractPartakeControllerTest {

    @Test
    public void testWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/auth/logout");
        loginAs(proxy, TestDataProvider.USER_ID1);
        
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
