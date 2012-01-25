package in.partake.controller.api.user;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Ignore;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class UserAPITest extends APIControllerTest {

    // User は、取得インタフェースはあるものの、基本的にはどのデータも非公開であるため、
    // ログインなしに取得することができるデータは限られている必要がある。
    @Test
    public void testGetUserWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/");
        addParameter(proxy, "userId", TestDataProvider.USER_ID1);
        
        proxy.execute();
        assertResultOK(proxy);

        // User.java から取得できるもの
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals(TestDataProvider.USER_ID1, obj.get("id"));
        Assert.assertEquals("1", obj.get("twitterId"));
        Assert.assertNull(obj.get("lastLoginAt")); // 取得できない
        Assert.assertNull(obj.get("calendarId"));  // calendar Id も取得できない。
        
        // Twitter Linkage を用いて取得できるもの
        JSONObject twitter = obj.getJSONObject("twitterLinkage");
        Assert.assertNotNull(twitter);
        Assert.assertEquals("testUser", twitter.get("screenName"));
        Assert.assertEquals("testUser", twitter.get("name"));
        Assert.assertNull(twitter.get("accessToken")); // 取得できない
        Assert.assertNull(twitter.get("accessTokenSecret")); // 取得できない
        Assert.assertEquals("http://www.example.com/", twitter.get("profileImageURL"));
        
        // OpenID Linkage は取得できない
        JSONObject openIDLinkage = obj.getJSONObject("openIDLinkage");
        Assert.assertNull(openIDLinkage);
    }
    
    @Test
    public void testGetUserWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/");
        addParameter(proxy, "userId", TestDataProvider.USER_ID1);
        
        // User 1 としてログイン
        loginAs(proxy, TestDataProvider.USER_ID1);
        
        proxy.execute();
        assertResultOK(proxy);

        // User.java から取得できるもの
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals(TestDataProvider.USER_ID1, obj.get("id"));
        Assert.assertEquals("1", obj.get("twitterId"));
        Assert.assertNotNull(obj.get("lastLoginAt")); // 取得でき*る*
        Assert.assertNotNull(obj.get("calendarId"));  // calendar Id も取得でき*る*
        JSONObject twitter = obj.getJSONObject("twitter");
        
        // Twitter Linkage を用いて取得できるもの
        Assert.assertNotNull(twitter);
        Assert.assertEquals("testUser", twitter.get("screenName"));
        Assert.assertEquals("testUser", twitter.get("name"));
        Assert.assertNull(twitter.get("accessToken")); // ログインしても取得できない
        Assert.assertNull(twitter.get("accessTokenSecret")); // ログインしても取得できない
        Assert.assertEquals("http://www.example.com/", twitter.get("profileImageURL"));
        
        // OpenID Linkage も取得でき*る*
        JSONArray openIDLinkage = obj.getJSONArray("openIDLinkage");
        Assert.assertNotNull(openIDLinkage);
        Assert.assertEquals(2, openIDLinkage.size());
        List<String> correctIDs = Arrays.asList(new String[] {
                "http://www.example.com/testuser",
                "http://www.example.com/testuser-alternative"
        });
        for (int i = 0; i < 2; ++i) {
            Assert.assertTrue(correctIDs.contains(openIDLinkage.get(i)));
        }
    }
    
    @Test
    public void testGetUserWithLoginAsAnotherUser() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/");
        addParameter(proxy, "userId", TestDataProvider.USER_ID1); 

        // USER 1 ではなく、USER 2 としてログイン。
        // この場合、login してない場合と同じ情報が得られなければならない。
        loginAs(proxy, TestDataProvider.USER_ID1);

        proxy.execute();
        assertResultOK(proxy);

        // User.java から取得できるもの
        JSONObject obj = getJSON(proxy);
        Assert.assertEquals(TestDataProvider.USER_ID1, obj.get("id"));
        Assert.assertEquals("1", obj.get("twitterId"));
        Assert.assertNull(obj.get("lastLoginAt")); // 取得できない
        Assert.assertNull(obj.get("calendarId"));  // calendar Id も取得できない。
        
        // Twitter Linkage を用いて取得できるもの
        JSONObject twitter = obj.getJSONObject("twitterLinkage");
        Assert.assertNotNull(twitter);
        Assert.assertEquals("testUser", twitter.get("screenName"));
        Assert.assertEquals("testUser", twitter.get("name"));
        Assert.assertNull(twitter.get("accessToken")); // 取得できない
        Assert.assertNull(twitter.get("accessTokenSecret")); // 取得できない
        Assert.assertEquals("http://www.example.com/", twitter.get("profileImageURL"));
        
        // OpenID Linkage は取得できない
        JSONObject openIDLinkage = obj.getJSONObject("openIDLinkage");
        Assert.assertNull(openIDLinkage);
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToGetEventsWithLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/events");
        addParameter(proxy, "userId", TestDataProvider.USER_ID1);
        
        loginAs(proxy, TestDataProvider.USER_ID1);
        
        proxy.execute();
        assertResultOK(proxy);
        
        throw new RuntimeException("Not implemented yet");
    }
    
    @Test
    @Ignore("Not implemented yet")
    public void testToGetEventsWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/events");
        addParameter(proxy, "userId", TestDataProvider.USER_ID1);

        loginAs(proxy, TestDataProvider.USER_ID1);

        proxy.execute();
        assertResultOK(proxy);
        
        // NOTE: the same data should be available if not logged in. 
        
        throw new RuntimeException("Not implemented yet");
    }
    
    @Test
    @Ignore("Not implemented yet")
    public void testToGetInvalidUserEvent() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/events");
        addParameter(proxy, "userId", TestDataProvider.INVALID_USER_ID);

        proxy.execute();
        assertResultInvalid(proxy);
    }


}
