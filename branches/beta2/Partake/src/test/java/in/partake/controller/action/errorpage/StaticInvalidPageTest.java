package in.partake.controller.action.errorpage;

import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class StaticInvalidPageTest extends AbstractPartakeControllerTest {
    @Test
    public void testAccessWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/invalid");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        
        proxy.execute();
        assertResultSuccess(proxy);
   }

    @Test
    public void testAccessWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/invalid");

        proxy.execute();
        assertResultSuccess(proxy);
    }
}
