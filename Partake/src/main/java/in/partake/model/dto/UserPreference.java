package in.partake.model.dto;

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
    // accessors
    
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
