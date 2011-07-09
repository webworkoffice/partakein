package in.partake.controller.api.account;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;

import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class SessionActionTest extends APIControllerTest {

    @Test
    public void testToGetSessionTokenWithoutLogin() throws Exception {
        // Even if not logged in, the token session should be available.
        
        ActionProxy proxy = getActionProxy("/api/account/getSessionToken");

        proxy.execute();
        assertResultOK(proxy);
        
        JSONObject obj = getJSON(proxy);
        Assert.assertNotNull(obj.get("token"));
    }

    @Test
    public void testToGetSessionTokenWithLogin() throws Exception {
        // If logged in, the token session should be available also.
        
        ActionProxy proxy = getActionProxy("/api/account/getSessionToken");

        loginAs(proxy, TestDataProvider.USER_ID1);
        
        proxy.execute();
        assertResultOK(proxy);
        
        JSONObject obj = getJSON(proxy);
        Assert.assertNotNull(obj.get("token"));
    }

}
