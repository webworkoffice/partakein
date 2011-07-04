package in.partake.model;

import net.sf.json.JSONObject;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.resource.PartakeProperties;

/**
 * user with related data.
 * @author shinyak
 *
 */
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
    
    public JSONObject toSafeJSON(boolean withTwitter) {
    	JSONObject obj = super.toSafeJSON();
    	
    	assert obj.get("twitter") == null;
    	
    	if (withTwitter) {
    		obj.put("twitterLinkage", twitterLinkage.toJSON());
    	}
    	
    	return obj;
    }
}
