package in.partake.resource;

/**
 * ServerErrorCode describes the reason why the server returned an error. 
 * @author shinyak
 */
public enum ServerErrorCode {
    UNKNOWN_ERROR("error.unknown"),
    INTENTIONAL_ERROR("error.intentional"),
    LOGIC_ERROR("error.logic_error"), // some logic error
    
    // TODO: DB_ERROR は後で名前かえるべき直す (or id をかえる)
    DB_ERROR("in.partake.database_error"),

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
