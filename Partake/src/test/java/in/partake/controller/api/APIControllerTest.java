package in.partake.controller.api;

import in.partake.controller.PartakeControllerTestCase;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import net.sf.json.JSONObject;

import com.opensymphony.xwork2.ActionProxy;


public abstract class APIControllerTest extends PartakeControllerTestCase {

    /**
     * proxy から JSON を取得する。取得できなかった場合は null を返す。
     * @param proxy
     * @return
     * @throws IOException
     */
    protected String getJSONString(ActionProxy proxy) throws Exception {
        return response.getContentAsString();
    }

    /**
     * proxy から JSON を取得する。
     * @param proxy
     * @return
     * @throws IOException
     */
    protected JSONObject getJSON(ActionProxy proxy) throws Exception {
        String str = getJSONString(proxy);
        System.out.println(str);
        return JSONObject.fromObject(str);
    }

    // ----------------------------------------------------------------------
    
    protected void assertOK(ActionProxy proxy) throws Exception {
        Assert.assertEquals(200, response.getStatus());
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("ok", obj.get("result"));
    }

    protected void assertResultInvalid(ActionProxy proxy) throws Exception {
        Assert.assertEquals(400, response.getStatus());        

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("invalid", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }
    
    protected void assertResultLoginRequired(ActionProxy proxy) throws Exception {
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

    protected void assertResultForbidden(ActionProxy proxy) throws Exception {
        // status code should be 403
        Assert.assertEquals(403, response.getStatus());
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("forbidden", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }

    protected void assertResultError(ActionProxy proxy) throws Exception {
        Assert.assertEquals(500, response.getStatus());
        
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals("error", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }
}
