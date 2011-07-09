package in.partake.controller.api.account;

import in.partake.controller.api.APIControllerTest;
import in.partake.service.UserService;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class OpenIDAPITest extends APIControllerTest {

    @Test
    public void testToRemoveOpenID() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        loginAs(proxy, "openid-remove-0");
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-0");
        
        proxy.execute();
        assertResultOK(proxy);
        
        // Check the proxy is really removed.
        List<String> identifiers = UserService.get().getOpenIDIdentifiers("openid-remove-0");
        Assert.assertNotNull(identifiers);
        Assert.assertFalse(identifiers.contains("http://www.example.com/openid-remove-0"));
    }

    @Test
    public void testToRemoveOpenIDWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        // When not login, should fail.
        
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-1");
        
        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToRemoveOpenIDWithInvalidLogin() throws Exception {
        // openid-remove-0 user does not have openid-remove-2 identifier.
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        loginAs(proxy, "openid-remove-0");
        
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-2");
        
        proxy.execute();
        assertResultInvalid(proxy);
    }

}
