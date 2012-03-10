package in.partake.controller.action.errorpage;

import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class StaticNotFoundPageTest extends AbstractPartakeControllerTest {
    @Test
    public void testAccessWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/notfound");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        
        proxy.execute();
        assertResultSuccess(proxy);
   }

    @Test
    public void testAccessWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/notfound");

        proxy.execute();
        assertResultSuccess(proxy);
    }
}
