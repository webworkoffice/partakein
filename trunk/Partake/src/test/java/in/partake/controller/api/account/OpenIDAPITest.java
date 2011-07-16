package in.partake.controller.api.account;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;
import in.partake.service.UserService;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class OpenIDAPITest extends APIControllerTest {

    @Test
    public void testToRemoveOpenID() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        loginAs(proxy, "openid-remove-0");
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-0");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultOK(proxy);
        
        // Check the OpenID has been really removed.
        List<String> identifiers = UserService.get().getOpenIDIdentifiers("openid-remove-0");
        Assert.assertNotNull(identifiers);
        Assert.assertFalse(identifiers.contains("http://www.example.com/openid-remove-0"));
    }

    @Test
    public void testToRemoveOpenIDWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        // When not login, should fail.
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-1");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToRemoveOpenIDWithInvalidLogin() throws Exception {
        // openid-remove-0 user does not have openid-remove-2 identifier.
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        loginAs(proxy, "openid-remove-0");
        
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-2");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testToRemoveOpenIDWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        // Check CSRF prevention works.
        loginAs(proxy, "openid-remove-3");
        
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-3");
        addInvalidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }
    
    @Test
    public void testToGetOpenIDWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/getOpenID");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }
    
    @Test
    public void testToGetOpenIDWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/getOpenID");

        loginAs(proxy, TestDataProvider.USER_ID1);
        
        proxy.execute();
        assertResultOK(proxy);
        
        JSONObject json = getJSON(proxy);
        JSONArray identifiers = json.getJSONArray("identifiers");

        Assert.assertEquals(2, identifiers.size());
        Assert.assertTrue(identifiers.contains("http://www.example.com/testuser"));
        Assert.assertTrue(identifiers.contains("http://www.example.com/testuser-alternative"));
    }
}
