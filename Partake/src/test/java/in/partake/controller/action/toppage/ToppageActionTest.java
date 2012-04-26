package in.partake.controller.action.toppage;

import in.partake.controller.action.ActionControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class ToppageActionTest extends ActionControllerTest {
    @Test
    public void testToExecute() throws Exception {
        ActionProxy proxy = getActionProxy("/");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        ToppageAction action = (ToppageAction) proxy.getAction();
        Assert.assertNotNull(action.getEnrolledEvents());
        Assert.assertNotNull(action.getOwnedEvents());
        Assert.assertNotNull(action.getEnrolledEvents());

        // TODO: We should test that getEnrolledEvents, getOwnedEvents, and getEnrolledEvents
        // should have the correct events here. However, this can be easily changed if TestDataProvider
        // is updated. So maybe we should consider the test data in TestDataProvider stable, or
        // TestDataProvider provides a method to return these necessary events.
    }

    @Test
    public void testToExecuteWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/");

        proxy.execute();
        assertResultSuccess(proxy);

        ToppageAction action = (ToppageAction) proxy.getAction();
        Assert.assertNotNull(action.getRecentEvents());
        Assert.assertNull(action.getOwnedEvents());
        Assert.assertNull(action.getEnrolledEvents());

        // TODO: getEnrolledEvents() should be tested here. Unfortunately we have the same problem
        // for testToExecuteWithLogin(). So it's a bit complicated to test it here.
    }
}
