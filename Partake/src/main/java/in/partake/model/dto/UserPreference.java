package in.partake.model.dto;

import org.apache.commons.lang.ObjectUtils;

public class UserPreference extends PartakeModel<UserPreference>{
    private boolean profilePublic;
    private boolean receivingTwitterMessage;
    private boolean tweetingAttendanceAutomatically;
    
    public static UserPreference getDefaultPreference() {
        return new UserPreference(true, true, false);
    }
    
    public UserPreference(boolean profilePublic, boolean receivingTwitterMessage, boolean tweetingAttendanceAutomatically) {
        this.profilePublic = profilePublic;
        this.receivingTwitterMessage = receivingTwitterMessage;
        this.tweetingAttendanceAutomatically = tweetingAttendanceAutomatically;
    }

    public UserPreference(UserPreference pref) {
        this.profilePublic = pref.profilePublic;
        this.receivingTwitterMessage = pref.receivingTwitterMessage;
        this.tweetingAttendanceAutomatically = pref.tweetingAttendanceAutomatically;
    }
    
    // ---------------------------------------------------------------
    // equals method
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserPreference)) { return false; }
        
        UserPreference lhs = this;
        UserPreference rhs = (UserPreference) obj;
        
        if (!ObjectUtils.equals(lhs.profilePublic, rhs.profilePublic)) { return false; }
        if (!ObjectUtils.equals(lhs.receivingTwitterMessage, rhs.receivingTwitterMessage)) { return false; }
        if (!ObjectUtils.equals(lhs.tweetingAttendanceAutomatically, rhs.tweetingAttendanceAutomatically)) { return false; }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        
        code = code * 37 + ObjectUtils.hashCode(profilePublic);
        code = code * 37 + ObjectUtils.hashCode(receivingTwitterMessage);
        code = code * 37 + ObjectUtils.hashCode(tweetingAttendanceAutomatically);
        
        return code;
    }
    
    // ---------------------------------------------------------------
    // accessors
    
    public boolean isProfilePublic() {
        return profilePublic;
    }

    public boolean isReceivingTwitterMessage() {
        return receivingTwitterMessage;
    }
    
    public boolean tweetsAttendanceAutomatically() {
        return tweetingAttendanceAutomatically;
    }

    public void setTweetingAttendanceAutomatically(boolean tweetingAttendanceAutomatically) {
        checkFrozen();
        this.tweetingAttendanceAutomatically = tweetingAttendanceAutomatically;
    }

    public void setProfilePublic(boolean profilePublic) {
        checkFrozen();
        this.profilePublic = profilePublic;
    }

    public void setReceivingTwitterMessage(boolean receivingTwitterMessage) {
        checkFrozen();
        this.receivingTwitterMessage = receivingTwitterMessage;
    }
    

	

}
