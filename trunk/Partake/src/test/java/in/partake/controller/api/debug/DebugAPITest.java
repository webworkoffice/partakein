package in.partake.controller.api.debug;

import in.partake.controller.api.APIControllerTest;
import in.partake.resource.I18n;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
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
    public void testEchoWithData() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/echo");
        
        addParameter(proxy, "data", "test");

        String r = proxy.execute();
        Assert.assertEquals("json", r);

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("ok", obj.get("result"));
        Assert.assertEquals("test", obj.get("data"));

        Assert.assertEquals(200, response.getStatus());        
    }

    @Test
    public void testEchoWithoutData() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/echo");
        
        String r = proxy.execute();
        Assert.assertEquals("json", r);

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("invalid", obj.get("result"));

        Assert.assertEquals(400, response.getStatus());        
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
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
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }

    @Test
    public void testInvalid() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/invalid");
        
        proxy.execute();
        
        Assert.assertEquals(400, response.getStatus());
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("invalid", obj.get("result"));        
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }
    
    @Test
    public void testError() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/error");
        
        proxy.execute();
        
        Assert.assertEquals(500, response.getStatus());
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("error", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }

    @Test
    public void testErrorException() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/errorException");
        
        proxy.execute();
        
        Assert.assertEquals(500, response.getStatus());
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("error", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }

    @Test
    public void testErrorDB() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/errorDB");
        
        proxy.execute();
        
        Assert.assertEquals(500, response.getStatus());
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("error", obj.get("result"));
        Assert.assertEquals(I18n.t(I18n.DATABASE_ERROR), obj.get("reason"));
    }
    
    @Test
    public void testErrorDBException() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/errorDBException");
        
        proxy.execute();
        
        Assert.assertEquals(500, response.getStatus());
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("error", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }

}
