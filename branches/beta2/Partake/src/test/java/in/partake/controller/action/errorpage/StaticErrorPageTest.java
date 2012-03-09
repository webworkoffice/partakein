package in.partake.controller.action.errorpage;

import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class StaticErrorPageTest extends AbstractPartakeControllerTest {
    @Test
    public void testAccessWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/error");
        loginAs(proxy, TestDataProvider.USER_ID1);
        
        proxy.execute();
        assertResultSuccess(proxy);
   }

    @Test
    public void testAccessWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/error");

        proxy.execute();
        assertResultSuccess(proxy);
    }
}
