package in.partake.controller.action;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Assert;

import com.opensymphony.xwork2.ActionProxy;

import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.resource.UserErrorCode;

public abstract class ActionControllerTest extends AbstractPartakeControllerTest {
    protected void assertResultSuccess(ActionProxy proxy) throws Exception {
        Assert.assertTrue(proxy.getAction() instanceof AbstractPartakeAction);
        Assert.assertEquals(200, response.getStatus());
    }

    protected void assertResultSuccess(ActionProxy proxy, String jspLocation) throws Exception {
        Assert.assertTrue(proxy.getAction() instanceof AbstractPartakeAction);
        Assert.assertEquals(200, response.getStatus());

        AbstractPartakeAction action = (AbstractPartakeAction) proxy.getAction();
        assertThat(action.getLocation(), is(jspLocation));
    }

    protected void assertResultInvalid(ActionProxy proxy) throws Exception {
        // Assert.assertEquals(400, response.getStatus());
        Assert.assertTrue(response.getRedirectedUrl().startsWith("/invalid"));
    }

    protected void assertResultInvalid(ActionProxy proxy, UserErrorCode ec) throws Exception {
        Assert.assertTrue(response.getRedirectedUrl().startsWith("/invalid?errorCode=" + ec.getErrorCode()));
    }

}
