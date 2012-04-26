package in.partake.model.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

@Entity(name = "TwitterLinkages")
public class UserTwitterLink extends PartakeModel<UserTwitterLink> {
    @Id
    private String twitterId;
    @Column
    private String screenName;
    @Column
    private String name;
    @Column
    private String accessToken;
    @Column
    private String accessTokenSecret;
    @Column(length = 4096)
    private String profileImageURL;
    @Column
    private String userId;

    public UserTwitterLink(String twitterId, String screenName, String name, String accessToken, String accessTokenSecret, String profileImageURL, String userId) {
        this.twitterId = twitterId;
        this.screenName = screenName;
        this.name = name;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.profileImageURL = profileImageURL;
        this.userId = userId;
    }

    public UserTwitterLink(long twitterId, String screenName, String name, String accessToken, String accessTokenSecret, String profileImageURL, String userId) {
        this(String.valueOf(twitterId), screenName, name, accessToken, accessTokenSecret, profileImageURL, userId);
    }

    public UserTwitterLink(UserTwitterLink linkage) {
        this.twitterId = linkage.twitterId;
        this.screenName = linkage.screenName;
        this.name = linkage.name;
        this.accessToken = linkage.accessToken;
        this.accessTokenSecret = linkage.accessTokenSecret;
        this.profileImageURL = linkage.profileImageURL;
        this.userId = linkage.userId;
    }

    public UserTwitterLink(JSONObject obj) {
        this.twitterId = obj.getString("twitterId");
        this.screenName = obj.getString("screenName");
        this.name = obj.getString("name");
        this.accessToken = obj.getString("accessToken");
        this.accessTokenSecret = obj.getString("accessTokenSecret");
        this.profileImageURL = obj.getString("profileImageURL");
        this.userId = obj.getString("userId");

    }

    @Override
    public Object getPrimaryKey() {
        return twitterId;
    }

    public JSONObject toSafeJSON() {
        JSONObject obj = new JSONObject();

        obj.put("twitterId", twitterId);
        obj.put("screenName", screenName);
        obj.put("name", name);
        obj.put("profileImageURL", profileImageURL);

        return obj;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("twitterId", twitterId);
        obj.put("screenName", screenName);
        obj.put("name", name);
        obj.put("accessToken", accessToken);
        obj.put("accessTokenSecret", accessTokenSecret);
        obj.put("profileImageURL", profileImageURL);
        obj.put("userId", userId);
        return obj;
    }

    // ----------------------------------------------------------------------
    // equals method

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserTwitterLink)) { return false; }

        UserTwitterLink lhs = this;
        UserTwitterLink rhs = (UserTwitterLink) obj;

        if (!ObjectUtils.equals(lhs.twitterId,         rhs.twitterId)) { return false; }
        if (!ObjectUtils.equals(lhs.screenName,        rhs.screenName)) { return false; }
        if (!ObjectUtils.equals(lhs.name,              rhs.name)) { return false; }
        if (!ObjectUtils.equals(lhs.accessToken,       rhs.accessToken)) { return false; }
        if (!ObjectUtils.equals(lhs.accessTokenSecret, rhs.accessTokenSecret)) { return false; }
        if (!ObjectUtils.equals(lhs.profileImageURL,   rhs.profileImageURL)) { return false; }
        if (!ObjectUtils.equals(lhs.userId,            rhs.userId)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(twitterId);
        code = code * 37 + ObjectUtils.hashCode(screenName);
        code = code * 37 + ObjectUtils.hashCode(name);
        code = code * 37 + ObjectUtils.hashCode(accessToken);
        code = code * 37 + ObjectUtils.hashCode(accessTokenSecret);
        code = code * 37 + ObjectUtils.hashCode(profileImageURL);
        code = code * 37 + ObjectUtils.hashCode(userId);

        return code;
    }

    // ----------------------------------------------------------------------
    //

    public String getTwitterId() {
        return twitterId;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getName() {
        return name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getUserId() {
        return userId;
    }

    public void setTwitterId(String twitterId) {
        checkFrozen();
        this.twitterId = twitterId;
    }

    public void setTwitterId(int twitterId) {
        checkFrozen();
        this.twitterId = String.valueOf(twitterId);
    }

    public void setScreenName(String screenName) {
        checkFrozen();
        this.screenName = screenName;
    }

    public void setName(String name) {
        checkFrozen();
        this.name = name;
    }

    public void setAccessToken(String accessToken) {
        checkFrozen();
        this.accessToken = accessToken;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        checkFrozen();
        this.accessTokenSecret = accessTokenSecret;
    }

    public void setProfileImageURL(String profileImageURL) {
        checkFrozen();
        this.profileImageURL = profileImageURL;
    }

    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }

    /**
     * mark this linkage as unauthorized one.
     * @see http://dev.twitter.com/pages/auth
     */
    public void markAsUnauthorized() {
        checkFrozen();
        this.accessToken = null;
        this.accessTokenSecret = null;
    }

    /**
     * @return true if this is authorized user.
     */
    public boolean isAuthorized() {
        return this.accessToken != null && this.accessTokenSecret != null;
    }
}
