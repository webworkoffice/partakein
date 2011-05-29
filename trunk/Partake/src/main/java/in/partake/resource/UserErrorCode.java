package in.partake.resource;

/**
 * UserErrorCode describes why the user request is invalid. 
 * @author shinyak
 *
 */
public enum UserErrorCode {
    INVALID_EVENT_ID("invalid.invalid_userid");
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
