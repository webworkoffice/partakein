package in.partake.model.dto;

public final class TwitterLinkage extends PartakeModel<TwitterLinkage> {
    private int twitterId;
    private String screenName;
    private String name;
    private String accessToken;
    private String accessTokenSecret;
    private String profileImageURL;
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
