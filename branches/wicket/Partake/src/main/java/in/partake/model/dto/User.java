package in.partake.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;

@Entity(name = "Users")
public class User extends PartakeModel<User> {
    private static final long serialVersionUID = 1L;
    
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
        this.lastLoginAt = lastLoginAt;
        this.twitterId = String.valueOf(twitterId);
        this.calendarId = calendarId;
    }

    public User(String id, int twitterId, Date lastLoginAt, String calendarId) {
        this(id, String.valueOf(twitterId), lastLoginAt, calendarId);
    }

    public User(User user) {
        this.id = user.id;
        this.lastLoginAt = user.lastLoginAt;
        this.twitterId = user.twitterId;
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
