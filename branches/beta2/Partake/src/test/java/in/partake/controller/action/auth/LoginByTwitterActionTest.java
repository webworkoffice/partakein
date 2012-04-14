package in.partake.controller.action.auth;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.resource.ServerErrorCode;
import in.partake.session.PartakeSession;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class LoginByTwitterActionTest extends AbstractPartakeControllerTest {
    @Test
    public void testWithValidVerifierLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/auth/verifyForTwitter");
        proxy.execute();

        assertRedirectedTo("http://www.example.com/validAuthenticationURL");

        PartakeSession session = getPartakeSession(proxy);
        assertThat(session.takeTwitterLoginInformation(), is(not(nullValue())));
    }

    @Test
    public void testLoginWithOAuthError() throws Exception {
        ActionProxy proxy = getActionProxy("/auth/loginByTwitter");
        addParameter(proxy, "redirectURL", "http://www.example.com/throwException");
        proxy.execute();

        assertResultError(proxy, ServerErrorCode.TWITTER_OAUTH_ERROR);
    }

}
