package in.partake.controller.action.errorpage;

import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class StaticLoginRequiredPageTest extends AbstractPartakeControllerTest {
    @Test
    public void testAccessWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/loginRequired");
        loginAs(proxy, TestDataProvider.USER_ID1);
        
        proxy.execute();
        assertResultRedirect(proxy, "/");
   }

    @Test
    public void testAccessWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/loginRequired");

        proxy.execute();
        assertResultSuccess(proxy);
    }
}
