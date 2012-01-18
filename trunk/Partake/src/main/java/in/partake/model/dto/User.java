package in.partake.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

@Entity(name = "Users")
public class User extends PartakeModel<User> {
    @Id
    private String  id;
    @Column
    private String  twitterId;
    @Column
    private Date    lastLoginAt; //
    @Column
    private String  calendarId; // TODO: なんでここに calendarId がいるんだっけ...
    
    public User() {
        // do nothing
    }
    
    public User(String id, String twitterId, Date lastLoginAt, String calendarId) {
        this.id = id;
        this.twitterId = String.valueOf(twitterId);
        this.lastLoginAt = lastLoginAt;
        this.calendarId = calendarId;
    }

    public User(String id, int twitterId, Date lastLoginAt, String calendarId) {
        this(id, String.valueOf(twitterId), lastLoginAt, calendarId);
    }

    public User(User user) {
        this.id = user.id;
        this.twitterId = user.twitterId;
        this.lastLoginAt = user.lastLoginAt;
        this.calendarId = user.calendarId;
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }
    
    @Override
    public User copy() {
        return new User(this);
    }
    
    /**
     * sensitive な情報を含まないような user を取得します。
     * id 及び twitter id のみが取得できます。
     * @return
     */
    public JSONObject toSafeJSON() {
    	JSONObject obj = new JSONObject();
    	obj.put("id", id);
    	obj.put("twitterId", twitterId);
    	
    	return obj;
    }
    
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("twitterId", twitterId);
        
        if (lastLoginAt != null)
            obj.put("lastLoginAt", lastLoginAt.getTime());
        
        if (calendarId != null)
            obj.put("calendarId", calendarId);

        return obj;
    }
    
    public static User fromJSON(JSONObject obj) {
        User user = new User();

        final String[] requiredFields = new String[] { "id", "twitterId" }; 
        for (String field : requiredFields) {
            if (!obj.containsKey(field))
                return null;
        }
        
        user.setId(obj.getString("id"));        
        user.setTwitterId(obj.getString("twitterId"));
        
        // lastLoginAt can be NULL.
        if (obj.containsKey("lastLoginAt"))
            user.setLastLoginAt(new Date(obj.getLong("lastLoginAt")));
        
        // calendarId can be NULL.
        if (obj.containsKey("calendarId"))
            user.setCalendarId(obj.getString("calendarId"));

        return user;
    }

    // ----------------------------------------------------------------------
    // equal methods
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) { return false; }
        
        User lhs = this;
        User rhs = (User) obj;
        
        if (!ObjectUtils.equals(lhs.id,          rhs.id))          { return false; }
        if (!ObjectUtils.equals(lhs.lastLoginAt, rhs.lastLoginAt)) { return false; }
        if (!ObjectUtils.equals(lhs.twitterId,   rhs.twitterId))   { return false; }
        if (!ObjectUtils.equals(lhs.calendarId,  rhs.calendarId))  { return false; }
        return true;       
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        
        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(lastLoginAt);
        code = code * 37 + ObjectUtils.hashCode(twitterId);
        code = code * 37 + ObjectUtils.hashCode(calendarId);
        
        return code;
    }
    

    
    // ----------------------------------------------------------------------
    // accessors

    public String getId() {
        return id;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        checkFrozen();
        this.lastLoginAt = lastLoginAt;
    }

    public void setTwitterId(String twitterId) {
        checkFrozen();
        this.twitterId = twitterId;
    }

    public void setTwitterId(int twitterId) {
        setTwitterId(String.valueOf(twitterId));
    }

    public void setCalendarId(String calendarId) {
        checkFrozen();
        this.calendarId = calendarId;
    }
}
