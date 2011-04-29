package in.partake.controller.api.user;

import java.util.List;

import in.partake.controller.api.APIControllerTest;
import in.partake.service.UserService;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class OpenIDAPITest extends APIControllerTest {

    @Test
    public void testToRemoveOpenID() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/removeOpenID");

        loginAs(proxy, "openid-remove-0");
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-0");
        
        String r = proxy.execute();
        Assert.assertEquals("json", r);

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("ok", obj.get("result"));

        Assert.assertEquals(200, response.getStatus());
        
        // Check the proxy is really removed.
        List<String> identifiers = UserService.get().getOpenIDIdentifiers("openid-remove-0");
        Assert.assertNotNull(identifiers);
        Assert.assertFalse(identifiers.contains("http://www.example.com/openid-remove-0"));
    }

    @Test
    public void testToRemoveOpenIDWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/removeOpenID");

        // When not login, should fail.
        
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-1");
        
        String r = proxy.execute();
        Assert.assertEquals("json", r);

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("auth", obj.get("result"));

        Assert.assertEquals(401, response.getStatus());
    }

    @Test
    public void testToRemoveOpenIDWithInvalidLogin() throws Exception {
        // openid-remove-0 user does not have openid-remove-2 identifier.
        ActionProxy proxy = getActionProxy("/api/user/removeOpenID");

        loginAs(proxy, "openid-remove-0");
        
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-2");
        
        String r = proxy.execute();
        Assert.assertEquals("json", r);

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("invalid", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));

        Assert.assertEquals(400, response.getStatus());
    }

}
