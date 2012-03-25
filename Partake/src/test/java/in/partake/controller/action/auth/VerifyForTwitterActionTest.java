package in.partake.controller.action.auth;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.session.TwitterLoginInformation;

import org.junit.Test;
import org.mockito.Mockito;

import com.opensymphony.xwork2.ActionProxy;

public class VerifyForTwitterActionTest extends AbstractPartakeControllerTest {
    @Test
    public void testForValidVerifier() throws Exception {
        ActionProxy proxy = getActionProxy("/auth/verifyForTwitter");
        addParameter(proxy, "oauth_verifier", "valid");
        setValidLoginInformation(proxy);
        proxy.execute();

        assertRedirectedTo("/");

        assertThat(getPartakeSession(proxy).takeTwitterLoginInformation(), is(nullValue()));
    }

    private void setValidLoginInformation(ActionProxy proxy) {
        TwitterLoginInformation loginInformation = Mockito.mock(TwitterLoginInformation.class);
        getPartakeSession(proxy).setTwitterLoginInformation(loginInformation);
    }
}
