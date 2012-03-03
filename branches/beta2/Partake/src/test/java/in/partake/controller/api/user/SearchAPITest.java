package in.partake.controller.api.user;

import in.partake.controller.api.APIControllerTest;
import in.partake.controller.api.event.SearchAction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class SearchAPITest extends APIControllerTest {

    @Test
    public void testToSearchWithEmptyQuery() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");

        proxy.execute();
        assertResultOK(proxy);
        
        JSONObject obj = getJSON(proxy);
        JSONArray events = obj.getJSONArray("events");
        Assert.assertNotNull(events);
        Assert.assertTrue(events.size() > 0);
    }
        
    @Test
    public void testToSeachWithInvalidMaxNum1() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addParameter(proxy, "maxNum", "-1");

        proxy.execute();
        assertResultInvalid(proxy);
    }
    
    @Test
    public void testToSeachWithInvalidMaxNum2() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addParameter(proxy, "maxNum", "");  // should not be empty

        proxy.execute();
        assertResultInvalid(proxy);
    }
    
    @Test
    public void testToSeachWithInvalidMaxNum3() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addParameter(proxy, "maxNum", String.valueOf(0x100000001L));  // exceeds 32bit integer 0xFFFFFFF

        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testToSeachWithInvalidMaxNum4() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addParameter(proxy, "maxNum", String.valueOf(SearchAction.MAX_NUM + 1));

        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testToSearchWithInvalidArg1() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addParameter(proxy, "query", "ほげほげ\"");
        
        proxy.execute();
        assertResultInvalid(proxy);
    }
    
    @Test
    @Ignore
    public void testShouldWriteMoreTests() throws Exception {
        // TODO: もっとテストが必要        
    }
}
