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

    CALENDAR_CREATION_FAILURE("error.calendar.creation_failure"),
    CALENDAR_INVALID_FORMAT("error.calendar.invalid_format"),
    
    NO_CSRF_PREVENTION("error.no_csrf_prevention"),
    NO_CREATED_SESSION_TOKEN("error.no_created_session_token"),
    
    TWITTER_OAUTH_ERROR("error.twitter.oauth"),
    
    BITLY_ERROR("error.bitly"),
    
    LUCENE_INITIALIZATION_FAILURE("error.lucene.initialization_failure");
    
    // ----------------------------------------------------------------------
    private String errorDescriptionId;
    
    private ServerErrorCode(String errorReasonId) {
        this.errorDescriptionId = errorReasonId;
    }
    
    public String getReasonString() {
        return I18n.t(errorDescriptionId);
    }
    
}
