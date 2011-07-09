package in.partake.resource;

/**
 * ServerErrorCode describes the reason why the server returned an error. 
 * @author shinyak
 */
public enum ServerErrorCode {
    DB_ERROR("in.partake.database_error"),
    // TODO: あといろいろ追加する

    NO_CSRF_PREVENTION("error.no_csrf_prevention"),
    NO_CREATED_SESSION_TOKEN("error.no_created_session_token");
    
    // ----------------------------------------------------------------------
    private String errorDescriptionId;
    
    private ServerErrorCode(String errorReasonId) {
        this.errorDescriptionId = errorReasonId;
    }
    
    public String getReasonString() {
        return I18n.t(errorDescriptionId);
    }
    
}
