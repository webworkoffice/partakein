package in.partake.controller.api.account;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade;
import in.partake.model.dto.UserPreference;
import in.partake.model.fixture.TestDataProvider;
import junit.framework.Assert;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class SetPreferenceAPITest extends APIControllerTest {
    @Test
    public void testToSetPreferenceWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        UserPreference pref = DeprecatedUserDAOFacade.get().getUserPreference(TestDataProvider.DEFAULT_USER_ID);
        Assert.assertEquals(true, pref.isProfilePublic());
        Assert.assertEquals(true, pref.isReceivingTwitterMessage());
        Assert.assertEquals(false, pref.tweetsAttendanceAutomatically());

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
    public void testToSetPreferenceWithLoginWithoutPreference() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");
        loginAs(proxy, TestDataProvider.USER_WITHOUT_PREF_ID);

        addValidSessionTokenToParameter(proxy);
        addParameter(proxy, "profilePublic", "false");
        addParameter(proxy, "receivingTwitterMessage", "false");
        addParameter(proxy, "tweetingAttendanceAutomatically", "false");        
        proxy.execute();

        assertResultOK(proxy);

        UserPreference pref = DeprecatedUserDAOFacade.get().getUserPreference(TestDataProvider.USER_WITHOUT_PREF_ID);
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
        Assert.assertEquals(false, pref.tweetsAttendanceAutomatically());

        loginAs(proxy, TestDataProvider.DEFAULT_USER_ID);
        addValidSessionTokenToParameter(proxy);
        proxy.execute();

        assertResultOK(proxy);

        pref = DeprecatedUserDAOFacade.get().getUserPreference(TestDataProvider.DEFAULT_USER_ID);
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
        assertResultInvalid(proxy);
    }

    @Test
    public void testToSetPreferenceWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/setPreference");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }  
}
