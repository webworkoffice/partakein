package in.partake.controller.api.account;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.UserPreference;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class GetAPITest extends APIControllerTest {
    private final String SESSION_TOKEN_URL = "/api/account/sessionToken";

    @Test
    public void testToGetSessionTokenWithoutLogin() throws Exception {
        // Even if not logged in, the token session should be available.
        ActionProxy proxy = getActionProxy(SESSION_TOKEN_URL);

        proxy.execute();
        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);
        Assert.assertNotNull(obj.get("token"));
    }

    @Test
    public void testToGetSessionTokenWithLogin() throws Exception {
        // If logged in, the token session should be available also.
        ActionProxy proxy = getActionProxy(SESSION_TOKEN_URL);

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);
        Assert.assertNotNull(obj.get("token"));
    }

    @Test
    public void testToGetWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/");
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultOK(proxy);

        // Check JSON

        JSONObject obj = getJSON(proxy);
        assertThat((String) obj.get("id"), is(TestDataProvider.DEFAULT_USER_ID));

        // TODO: Checks Twitter?

        // Checks UserPreference.
        JSONObject prefObj = obj.getJSONObject("preference");
        UserPreference pref = UserPreference.getDefaultPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(pref.isProfilePublic(), prefObj.getBoolean("profilePublic"));
        Assert.assertEquals(pref.isReceivingTwitterMessage(), prefObj.getBoolean("receivingTwitterMessage"));
        Assert.assertEquals(pref.tweetsAttendanceAutomatically(), prefObj.getBoolean("tweetingAttendanceAutomatically"));

        // Checks OpenIds
        JSONArray array = obj.getJSONArray("openId");
        List<String> openIds = new ArrayList<String>();
        for (int i = 0; i < array.size(); ++i)
            openIds.add(array.getString(i));
        assertThat(openIds, hasItem(TestDataProvider.DEFAULT_USER_OPENID_IDENTIFIER));
        assertThat(openIds, hasItem(TestDataProvider.DEFAULT_USER_OPENID_ALTERNATIVE_IDENTIFIER));
    }

    @Test
    public void testToGetWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToSetPreferenceWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        UserPreference pref = loadUserPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(true, pref.isProfilePublic());
        Assert.assertEquals(true, pref.isReceivingTwitterMessage());
        Assert.assertEquals(false, pref.tweetsAttendanceAutomatically());

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);

        addValidSessionTokenToParameter(proxy);
        addParameter(proxy, "profilePublic", "false");
        addParameter(proxy, "receivingTwitterMessage", "false");
        addParameter(proxy, "tweetingAttendanceAutomatically", "true");
        proxy.execute();

        assertResultOK(proxy);

        pref = loadUserPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(false, pref.isProfilePublic());
        Assert.assertEquals(false, pref.isReceivingTwitterMessage());
        Assert.assertEquals(true, pref.tweetsAttendanceAutomatically());
    }

    @Test
    public void testToSetPreferenceWithLoginWithoutArgument() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        UserPreference pref = loadUserPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(true, pref.isProfilePublic());
        Assert.assertEquals(true, pref.isReceivingTwitterMessage());
        Assert.assertEquals(false, pref.tweetsAttendanceAutomatically());

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        addValidSessionTokenToParameter(proxy);
        proxy.execute();

        assertResultOK(proxy);

        pref = loadUserPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(true, pref.isProfilePublic());
        Assert.assertEquals(true, pref.isReceivingTwitterMessage());
        Assert.assertEquals(false, pref.tweetsAttendanceAutomatically());
    }

    @Test
    public void testToSetPreferenceWithLoginWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        addInvalidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

    @Test
    public void testToSetPreferenceWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }
}
