package in.partake.model.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;

@Entity
public class TwitterLinkage extends PartakeModel<TwitterLinkage> {
    @Id
    private int twitterId;
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
    
    public TwitterLinkage() {
        
    }
    
    public TwitterLinkage(int twitterId, String screenName, String name, String accessToken, String accessTokenSecret, String profileImageURL, String userId) {
        this.twitterId = twitterId;
        this.screenName = screenName;
        this.name = name;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.profileImageURL = profileImageURL;
        this.userId = userId;
    }

    @Override
    public Object getPrimaryKey() {
        return twitterId;
    }
    
    // ----------------------------------------------------------------------
    // equals method
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TwitterLinkage)) { return false; }
        
        TwitterLinkage lhs = this;
        TwitterLinkage rhs = (TwitterLinkage) obj;
        
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

    public int getTwitterId() {
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

    public void setTwitterId(int twitterId) {
        checkFrozen();
        this.twitterId = twitterId;
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
    
    
}
