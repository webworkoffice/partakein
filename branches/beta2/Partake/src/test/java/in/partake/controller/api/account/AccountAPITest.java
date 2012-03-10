package in.partake.controller.api.account;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade;
import in.partake.model.dto.UserPreference;
import in.partake.model.fixture.TestDataProvider;

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

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        
        proxy.execute();
        assertResultOK(proxy);
        
        JSONObject obj = getJSON(proxy);
        Assert.assertNotNull(obj.get("token"));
    }

    @Test
    public void testToGetWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/");

        final String userId = TestDataProvider.DEFAULT_USER_ID;
        
        loginAs(proxy, userId);
        
        proxy.execute();
        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);
        Assert.assertEquals(userId, obj.get("id"));

        // TODO: Checks Twitter?
        
        // Checks UserPreference.
        UserPreference pref = DeprecatedUserDAOFacade.get().getUserPreference(TestDataProvider.DEFAULT_USER_ID);
        JSONObject prefObj = obj.getJSONObject("preference");
        Assert.assertEquals(pref.isProfilePublic(), prefObj.getBoolean("profilePublic"));
        Assert.assertEquals(pref.isReceivingTwitterMessage(), prefObj.getBoolean("receivingTwitterMessage"));
        Assert.assertEquals(pref.tweetsAttendanceAutomatically(), prefObj.getBoolean("tweetingAttendanceAutomatically"));

        // Checks OpenIds
        List<String> openIds = DeprecatedUserDAOFacade.get().getOpenIDIdentifiers(userId);
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

        UserPreference pref = DeprecatedUserDAOFacade.get().getUserPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(true, pref.isProfilePublic());
        Assert.assertEquals(true, pref.isReceivingTwitterMessage());
        Assert.assertEquals(true, pref.tweetsAttendanceAutomatically());
        
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        
        addValidSessionTokenToParameter(proxy);
        addParameter(proxy, "profilePublic", "false");
        addParameter(proxy, "receivingTwitterMessage", "false");
        addParameter(proxy, "tweetingAttendanceAutomatically", "false");        
        proxy.execute();
        
        assertResultOK(proxy);
        
        pref = DeprecatedUserDAOFacade.get().getUserPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(false, pref.isProfilePublic());
        Assert.assertEquals(false, pref.isReceivingTwitterMessage());
        Assert.assertEquals(false, pref.tweetsAttendanceAutomatically());
    }
    
    @Test
    public void testToSetPreferenceWithLoginWithoutArgument() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        UserPreference pref = DeprecatedUserDAOFacade.get().getUserPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(true, pref.isProfilePublic());
        Assert.assertEquals(true, pref.isReceivingTwitterMessage());
        Assert.assertEquals(true, pref.tweetsAttendanceAutomatically());
        
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        addValidSessionTokenToParameter(proxy);
        proxy.execute();
        
        assertResultOK(proxy);
        
        pref = DeprecatedUserDAOFacade.get().getUserPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(true, pref.isProfilePublic());
        Assert.assertEquals(true, pref.isReceivingTwitterMessage());
        Assert.assertEquals(true, pref.tweetsAttendanceAutomatically());
    }
    
    @Test
    public void testToSetPreferenceWithLoginWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
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

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        addParameter(proxy, "identifier", TestDataProvider.DEFAULT_USER_OPENID_IDENTIFIER);
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultOK(proxy);
        
        // Check the OpenID has been really removed.
        List<String> identifiers = DeprecatedUserDAOFacade.get().getOpenIDIdentifiers(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertNotNull(identifiers);
        Assert.assertFalse(identifiers.contains(TestDataProvider.DEFAULT_USER_OPENID_IDENTIFIER));
    }

    @Test
    public void testToRemoveOpenIDWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        // When not login, should fail.
        addParameter(proxy, "identifier", TestDataProvider.DEFAULT_USER_OPENID_IDENTIFIER);
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToRemoveOpenIDWithInvalidLogin() throws Exception {
        // openid-remove-0 user does not have openid-remove-2 identifier.
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        loginAs(proxy, TestDataProvider.DEFAULT_ANOTHER_USER_ID);
        
        addParameter(proxy, "identifier", TestDataProvider.DEFAULT_USER_OPENID_IDENTIFIER);
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testToRemoveOpenIDWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/removeOpenID");

        // Check CSRF prevention works.
        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        
        addParameter(proxy, "identifier", TestDataProvider.DEFAULT_USER_OPENID_IDENTIFIER);
        addInvalidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }    
}
