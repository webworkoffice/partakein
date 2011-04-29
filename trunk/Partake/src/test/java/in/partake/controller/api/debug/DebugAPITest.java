package in.partake.controller.api.debug;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;
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

        proxy.execute();
        assertOK(proxy);
    }
    
    @Test
    public void testEchoWithData() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/echo");
        addParameter(proxy, "data", "test");

        proxy.execute();
        assertOK(proxy);

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("test", obj.get("data"));
    }

    @Test
    public void testEchoWithoutData() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/echo");
        
        proxy.execute();
        assertResultInvalid(proxy);
    }

    
    @Test
    public void testSuccessIfLoginWhenLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/successIfLogin");
        loginAs(proxy, TestDataProvider.USER_ID1);
        
        proxy.execute();
        assertOK(proxy);
    }
    
    @Test
    public void testSuccessIfLoginWhenNotLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/successIfLogin");
        
        // I don't care what proxy.execute returns.
        // However, http status code and header are tested.
        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testInvalid() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/invalid");
        
        proxy.execute();
        
        Assert.assertEquals(400, response.getStatus());
        assertResultInvalid(proxy);
    }
    
    @Test
    public void testError() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/error");
        
        proxy.execute();
        assertResultError(proxy);        
    }

    @Test
    public void testErrorException() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/errorException");
        
        proxy.execute();
        assertResultError(proxy);        
    }

    @Test
    public void testErrorDB() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/errorDB");
        
        proxy.execute();
        assertResultError(proxy);        
    }
    
    @Test
    public void testErrorDBException() throws Exception {
        ActionProxy proxy = getActionProxy("/debug/errorDBException");
        
        proxy.execute();
        assertResultError(proxy);        
    }

}
