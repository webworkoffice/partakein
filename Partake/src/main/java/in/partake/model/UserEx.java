package in.partake.model;

import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.resource.PartakeProperties;

/**
 * user with related data.
 * @author shinyak
 *
 */
public class UserEx extends User {
    private static final long serialVersionUID = 1L;
    
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
