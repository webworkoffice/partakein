package in.partake.model.dto;

import java.util.Date;

public class User extends PartakeModel<User> {
    private String  id;          // 
    private Date    lastLoginAt; // 
    private int     twitterId;
    private String  calendarId;
    
    public User() {        
    }
    
    public User(User user) {
        this.id = user.id;
        this.lastLoginAt = user.lastLoginAt;
        this.twitterId = user.twitterId;
        this.calendarId = user.calendarId;
    }

    public String getId() {
        return id;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public int getTwitterId() {
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

    public void setTwitterId(int twitterId) {
        checkFrozen();
        this.twitterId = twitterId;
    }

    public void setCalendarId(String calendarId) {
        checkFrozen();
        this.calendarId = calendarId;
    }
}
