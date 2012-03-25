package in.partake.controller.api.user;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;
import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class GetUserAPITest extends APIControllerTest {

    @Test
    public void testGetUser() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/");
        addParameter(proxy, "userId", TestDataProvider.DEFAULT_USER_ID);

        proxy.execute();
        assertResultOK(proxy);

        // User.java から取得できるもの
        JSONObject obj = getJSON(proxy);
        assertThat(obj.getString("id"), is(TestDataProvider.DEFAULT_USER_ID));
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
    public void testGetUserWithInvalidUserId() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/" + TestDataProvider.INVALID_USER_ID);
        proxy.execute();

        assertResultInvalid(proxy, UserErrorCode.INVALID_USER_ID);
    }

    @Test
    public void testGetUserWithoutUserId() throws Exception {
        ActionProxy proxy = getActionProxy("/api/user/");
        proxy.execute();

        assertResultInvalid(proxy, UserErrorCode.MISSING_USER_ID);
    }
}
