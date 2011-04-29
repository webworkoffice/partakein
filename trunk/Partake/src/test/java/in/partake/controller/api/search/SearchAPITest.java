package in.partake.controller.api.search;

import in.partake.controller.api.APIControllerTest;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class SearchAPITest extends APIControllerTest {

    @Test
    public void testToSearchWithEmptyQuery() throws Exception {
        ActionProxy proxy = getActionProxy("/api/search");

        proxy.execute();

        JSONObject obj = getJSON(proxy);
        
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("ok", obj.get("result"));
        
        // 任意のオーダーで Event が出力される。
        throw new RuntimeException("Not implemented yet");
    }
        
    @Test
    public void testToSeachWithInvalidMaxNum1() throws Exception {
        ActionProxy proxy = getActionProxy("/api/search");
        addParameter(proxy, "maxNum", "-1");

        proxy.execute();

        JSONObject obj = getJSON(proxy);
        
        Assert.assertEquals(400, response.getStatus());
        Assert.assertEquals("invalid", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }
    
    @Test
    public void testToSeachWithInvalidMaxNum2() throws Exception {
        ActionProxy proxy = getActionProxy("/api/search");
        addParameter(proxy, "maxNum", "");  // should not be empty

        proxy.execute();

        JSONObject obj = getJSON(proxy);
        
        Assert.assertEquals(400, response.getStatus());
        Assert.assertEquals("invalid", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }
    
    @Test
    public void testToSeachWithInvalidMaxNum3() throws Exception {
        ActionProxy proxy = getActionProxy("/api/search");
        addParameter(proxy, "maxNum", String.valueOf(0x100000001L));  // exceeds 32bit integer 0xFFFFFFF

        proxy.execute();

        JSONObject obj = getJSON(proxy);
        
        Assert.assertEquals(400, response.getStatus());
        Assert.assertEquals("invalid", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }

    @Test
    public void testToSeachWithInvalidMaxNum4() throws Exception {
        ActionProxy proxy = getActionProxy("/api/search");
        addParameter(proxy, "maxNum", SearchAction.MAX_NUM + 1);

        proxy.execute();

        JSONObject obj = getJSON(proxy);
        
        Assert.assertEquals(400, response.getStatus());
        Assert.assertEquals("invalid", obj.get("result"));
        Assert.assertFalse(StringUtils.isBlank((String) obj.get("reason")));
    }

}
