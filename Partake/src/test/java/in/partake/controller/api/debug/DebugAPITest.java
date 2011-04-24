package in.partake.controller.api.debug;

import in.partake.controller.api.APIControllerTest;
import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class DebugAPITest extends APIControllerTest {

    @Test
    public void testSuccess() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/success");

        String r = proxy.execute();
        Assert.assertEquals("json", r);

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("ok", obj.get("result"));

        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testSuccessIfLoginWhenLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/successIfLogin");
        login(proxy);
        
        String result = proxy.execute();
        Assert.assertEquals("json", result);

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("ok", obj.get("result"));
        
        Assert.assertEquals(200, response.getStatus());
    }
    
    @Test
    public void testSuccessIfLoginWhenNotLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/successIfLogin");
        
        // I don't care what proxy.execute returns.
        // However, http status code and header are tested.
        proxy.execute();
        
        // status code should be 401.
        Assert.assertEquals(401, response.getStatus());
        
        // header should contain WWW-authenticate.
        String authenticate = (String) response.getHeader("WWW-Authenticate");
        Assert.assertNotNull(authenticate);
        Assert.assertTrue(authenticate.contains("OAuth"));
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("auth", obj.get("result"));
    }

    @Test
    public void testError() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/error");
        
        proxy.execute();
        
        Assert.assertEquals(500, response.getStatus());
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("error", obj.get("result"));
    }
}
