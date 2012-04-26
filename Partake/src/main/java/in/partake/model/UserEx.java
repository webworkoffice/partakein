package in.partake.model;

import java.util.Set;

import net.sf.json.JSONObject;
import in.partake.model.dto.UserTwitterLink;
import in.partake.model.dto.User;
import in.partake.resource.PartakeProperties;

/**
 * user with related data.
 * @author shinyak
 *
 */
public class UserEx extends User {
    private UserTwitterLink twitterLinkage;

    public UserEx(User user, UserTwitterLink twitterLinkage) {
        super(user);
        this.twitterLinkage = twitterLinkage;
    }

    public UserTwitterLink getTwitterLinkage() {
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
        Set<String> adminScreenNames = PartakeProperties.get().getTwitterAdminNames();
        return adminScreenNames.contains(screenName);
    }

    public JSONObject toSafeJSON(boolean withTwitter) {
        JSONObject obj = super.toSafeJSON();

        assert obj.get("twitter") == null;

        if (withTwitter) {
            obj.put("twitterLinkage", twitterLinkage.toSafeJSON());
        }

        return obj;
    }
}
