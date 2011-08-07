package in.partake.controller.api.event;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;


public class SearchActionTest extends APIControllerTest {
    @Test
    public void testSearchEventAllCategory() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        assertThat(proxy.execute(), equalTo("json"));

        assertResultOK(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.containsKey("reason"), equalTo(false));
    }

    // =========================================================================
    // maxNum
    @Test
    public void testSearchEventTooLargeMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", "10000");
        assertThat(proxy.execute(), equalTo("json"));

        assertResultInvalid(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.getString("reason"), equalTo(UserErrorCode.INVALID_SEARCH_MAXNUM.getReasonString()));
    }

    @Test
    public void testSearchEventMissingMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", null);
        assertThat(proxy.execute(), equalTo("json"));

        assertResultInvalid(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.getString("reason"), equalTo(UserErrorCode.MISSING_SEARCH_MAXNUM.getReasonString()));
    }

    @Test
    public void testSearchEventEmptyMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", "");
        assertThat(proxy.execute(), equalTo("json"));

        assertResultInvalid(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.getString("reason"), equalTo(UserErrorCode.INVALID_SEARCH_MAXNUM.getReasonString()));
    }

    @Test
    public void testSearchEventNotNumberMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", "m(_ _)m");
        assertThat(proxy.execute(), equalTo("json"));

        assertResultInvalid(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.getString("reason"), equalTo(UserErrorCode.INVALID_SEARCH_MAXNUM.getReasonString()));
    }

    @Test
    public void testSearchEventNotLowerCamelMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", null);
        addParameter(proxy, "maxnum", "10");
        assertThat(proxy.execute(), equalTo("json"));

        assertResultInvalid(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.getString("reason"), equalTo(UserErrorCode.MISSING_SEARCH_MAXNUM.getReasonString()));
    }

    // =========================================================================
    // utility
    private void addBasicParameter(ActionProxy proxy) throws DAOException {
        addParameter(proxy, "query", "test");
        addParameter(proxy, "category", "all");
        addParameter(proxy, "beforeDeadlineOnly", "true");
        addParameter(proxy, "sortOrder", "score");
        addParameter(proxy, "maxNum", "10");
	}
}
