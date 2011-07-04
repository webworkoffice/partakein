package in.partake.resource;

/**
 * UserErrorCode describes why the user request is invalid. 
 * @author shinyak
 *
 */
public enum UserErrorCode {
    INVALID_EVENT_ID("invalid.invalid_eventid"),
    MISSING_EVENT_ID("invalid.missing_evnetid"),
    INVALID_USER_ID("invalid.invalid_userid"),
    MISSING_USER_ID("invalid.missing_userid");

    // TODO: あといろいろ追加する
    
    // ----------------------------------------------------------------------
    private String errorDescriptionId;
    
    private UserErrorCode(String errorReason) {
        this.errorDescriptionId = errorReason;
    }
    
    public String getReasonString() {
        return I18n.t(errorDescriptionId);
    }

}
