package in.partake.model.dto;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

public class User extends PartakeModel<User> {
    private String id;
    private String screenName;
    private String profileImageURL;

    public User(String id, String screenName, String profileImageURL) {
        this.id = id;
        this.screenName = screenName;
        this.profileImageURL = profileImageURL;
    }

    public User(User user) {
        this.id = user.id;
        this.screenName = user.screenName;
        this.profileImageURL = user.profileImageURL;
    }

    public User(JSONObject obj) {
        this.id = obj.getString("id");
        this.screenName = obj.getString("screenName");
        this.profileImageURL = obj.getString("profileImageURL");
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    /**
     * sensitive な情報を含まないような user を取得します。
     *
     * @return
     */
    public JSONObject toSafeJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("screenName", screenName);
        obj.put("profileImageURL", profileImageURL);

        return obj;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("screenName", screenName);
        obj.put("profileImageURL", profileImageURL);

        return obj;
    }

    // ----------------------------------------------------------------------
    // equal methods

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) { return false; }

        User lhs = this;
        User rhs = (User) obj;

        if (!ObjectUtils.equals(lhs.id, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.screenName, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.profileImageURL, rhs.id)) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(id);
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getId() {
        return id;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setScreenName(String screenName) {
        checkFrozen();
        this.screenName = screenName;
    }

    public void setProfileImageURL(String profileImageURL) {
        checkFrozen();
        this.profileImageURL = profileImageURL;
    }
}
