package in.partake.controller.api.account;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.UserPreference;
import in.partake.model.fixture.TestDataProvider;
import in.partake.service.UserService;

import java.util.Collections;
import java.util.List;

import junit.framework.Assert;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class AccountAPITest extends APIControllerTest {
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

        loginAs(proxy, TestDataProvider.USER_ID1);
        
        proxy.execute();
        assertResultOK(proxy);
        
        JSONObject obj = getJSON(proxy);
        Assert.assertNotNull(obj.get("token"));
    }

    @Test
    public void testToGetWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/");

        final String userId = TestDataProvider.USER_ID1;
        
        loginAs(proxy, userId);
        
        proxy.execute();
        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals(userId, obj.get("id"));

        // TODO: Checks Twitter?
        
        // Checks UserPreference.
        UserPreference pref = UserService.get().getUserPreference(TestDataProvider.USER_ID1);
        JSONObject prefObj = obj.getJSONObject("preference");
        Assert.assertEquals(pref.isProfilePublic(), prefObj.getBoolean("profilePublic"));
        Assert.assertEquals(pref.isReceivingTwitterMessage(), prefObj.getBoolean("receivingTwitterMessage"));
        Assert.assertEquals(pref.tweetsAttendanceAutomatically(), prefObj.getBoolean("tweetingAttendanceAutomatically"));

        // Checks OpenIds
        List<String> openIds = UserService.get().getOpenIDIdentifiers(userId);
        Collections.sort(openIds);
        
        JSONArray array = obj.getJSONArray("openId");
        Assert.assertEquals(openIds.size(), array.size());
        for (int i = 0; i < openIds.size(); ++i)
            Assert.assertEquals(openIds.get(i), array.getString(i)); 
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

        UserPreference pref = UserService.get().getUserPreference(TestDataProvider.USER_ID1);
        Assert.assertEquals(true, pref.isProfilePublic());
        Assert.assertEquals(true, pref.isReceivingTwitterMessage());
        Assert.assertEquals(true, pref.tweetsAttendanceAutomatically());
        
        loginAs(proxy, TestDataProvider.USER_ID1);
        
        addValidSessionTokenToParameter(proxy);
        addParameter(proxy, "profilePublic", "false");
        addParameter(proxy, "receivingTwitterMessage", "false");
        addParameter(proxy, "tweetingAttendanceAutomatically", "false");        
        proxy.execute();
        
        assertResultOK(proxy);
        
        pref = UserService.get().getUserPreference(TestDataProvider.USER_ID1);
        Assert.assertEquals(false, pref.isProfilePublic());
        Assert.assertEquals(false, pref.isReceivingTwitterMessage());
        Assert.assertEquals(false, pref.tweetsAttendanceAutomatically());
    }
    
    @Test
    public void testToSetPreferenceWithLoginWithoutArgument() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        UserPreference pref = UserService.get().getUserPreference(TestDataProvider.USER_ID1);
        Assert.assertEquals(true, pref.isProfilePublic());
        Assert.assertEquals(true, pref.isReceivingTwitterMessage());
        Assert.assertEquals(true, pref.tweetsAttendanceAutomatically());
        
        loginAs(proxy, TestDataProvider.USER_ID1);
        addValidSessionTokenToParameter(proxy);
        proxy.execute();
        
        assertResultOK(proxy);
        
        pref = UserService.get().getUserPreference(TestDataProvider.USER_ID1);
        Assert.assertEquals(true, pref.isProfilePublic());
        Assert.assertEquals(true, pref.isReceivingTwitterMessage());
        Assert.assertEquals(true, pref.tweetsAttendanceAutomatically());
    }
    
    @Test
    public void testToSetPreferenceWithLoginWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        loginAs(proxy, TestDataProvider.USER_ID1);
        addInvalidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }
    
    @Test
    public void testToSetPreferenceWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }
    
    @Test
    public void testToRemoveOpenID() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        loginAs(proxy, TestDataProvider.EVENT_REMOVE_ID0);
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-0");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultOK(proxy);
        
        // Check the OpenID has been really removed.
        List<String> identifiers = UserService.get().getOpenIDIdentifiers(TestDataProvider.EVENT_REMOVE_ID0);
        Assert.assertNotNull(identifiers);
        Assert.assertFalse(identifiers.contains("http://www.example.com/openid-remove-0"));
    }

    @Test
    public void testToRemoveOpenIDWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        // When not login, should fail.
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-1");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToRemoveOpenIDWithInvalidLogin() throws Exception {
        // openid-remove-0 user does not have openid-remove-2 identifier.
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        loginAs(proxy, TestDataProvider.EVENT_REMOVE_ID0);
        
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-2");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testToRemoveOpenIDWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        // Check CSRF prevention works.
        loginAs(proxy, TestDataProvider.EVENT_REMOVE_ID3);
        
        addParameter(proxy, "identifier", "http://www.example.com/openid-remove-3");
        addInvalidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }    
}
