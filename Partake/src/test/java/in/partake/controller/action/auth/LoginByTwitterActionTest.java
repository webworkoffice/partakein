package in.partake.controller.action.auth;

import in.partake.controller.action.ActionControllerTest;
import in.partake.resource.ServerErrorCode;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class LoginByTwitterActionTest extends ActionControllerTest {
    @Test
    public void testLoginWithOAuthError() throws Exception {
        ActionProxy proxy = getActionProxy("/auth/loginByTwitter");
        addParameter(proxy, "redirectURL", "http://www.example.com/throwException");
        proxy.execute();

        assertResultError(proxy, ServerErrorCode.TWITTER_OAUTH_ERROR);
    }

}
