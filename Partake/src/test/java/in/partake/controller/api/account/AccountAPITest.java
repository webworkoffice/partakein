package in.partake.controller.api.account;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;
import junit.framework.Assert;
import net.sf.json.JSONObject;

import org.junit.Ignore;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class AccountAPITest extends APIControllerTest {

    @Test
    @Ignore("Not implemented yet")
    public void testToGetWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/get");

        loginAs(proxy, TestDataProvider.USER_ID1);
        
        proxy.execute();
        assertResultOK(proxy);

        // check things expected comes.
        throw new RuntimeException("Not implemented yet");
    }
    
    @Test
    public void testToGetWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/get");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }
    
    @Test
    @Ignore("Not implemented yet")
    public void testToGetCalendarWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/getCalendar");

        loginAs(proxy, TestDataProvider.USER_ID1);
        
        proxy.execute();
        assertResultOK(proxy);

        // check things expected comes.
        throw new RuntimeException("Not implemented yet");
    }
    
    @Test
    @Ignore("Not implemented yet")
    public void testToGetCalendarWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/getCalendar");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToGetPreferenceWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/preference");

        loginAs(proxy, TestDataProvider.USER_ID1);
        
        proxy.execute();
        assertResultOK(proxy);
  
        JSONObject json = getJSON(proxy);
        Assert.assertEquals(true, json.getBoolean("profilePublic"));
        Assert.assertEquals(true, json.getBoolean("receivingTwitterMessage"));
        Assert.assertEquals(true, json.getBoolean("tweetingAttendanceAutomatically"));
    }
    
    @Test
    public void testToGetPreferenceWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/preference");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToSetPreferenceWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        loginAs(proxy, TestDataProvider.USER_ID1);
        addValidSessionTokenToParameter(proxy);
        // TODO: add some preference parameter to check 
        
        
        proxy.execute();
        assertResultOK(proxy);

        // check things expected comes.
        throw new RuntimeException("Not implemented yet");
    }
    
    @Test
    @Ignore("Not implemented yet")
    public void testToSetPreferenceWithLoginWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        loginAs(proxy, TestDataProvider.USER_ID1);
        addInvalidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }
    
    @Test
    @Ignore("Not implemented yet")
    public void testToSetPreferenceWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }
}
