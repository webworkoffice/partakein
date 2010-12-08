package in.partake.model;

import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.resource.PartakeProperties;

public class UserEx extends User {
    private TwitterLinkage twitterLinkage;
    
    public UserEx(User user, TwitterLinkage twitterLinkage) {
        super(user);
        this.twitterLinkage = twitterLinkage;
    }
    
    public TwitterLinkage getTwitterLinkage() {
        return twitterLinkage;
    }
    
    public String getScreenName() {
        return twitterLinkage.getScreenName();
    }
    
    public String getProfileImageURL() {
        return twitterLinkage.getProfileImageURL();
    }
    
    public boolean isAdministrator() {
        String screenName = twitterLinkage.getScreenName();
        return screenName.equals(PartakeProperties.get().getTwitterAdminName());
    }
}
