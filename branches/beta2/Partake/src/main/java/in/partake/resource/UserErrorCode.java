package in.partake.resource;

/**
 * UserErrorCode describes why the user request is invalid.
 * @author shinyak
 *
 */
public enum UserErrorCode {
    // TODO: 表記を揃える。invalid.<type>.<reason> に固定すること。
    
    //
    UNKNOWN_USER_ERROR("invalid.unknown"),
    INTENTIONAL_USER_ERROR("invalid.intentional"),
    UNEXPECTED_REQUEST("invalid.request.unexpected"),
    
    // BASE    
    INVALID_ARGUMENT("invalid.argument"),
    INVALID_PROHIBITED("invalid.prohibited", 403),
    
    // EVENT
    INVALID_EVENT_ID("invalid.event.id"),
    MISSING_EVENT_ID("invalid.event.id.missing"),

    INVALID_ENROLL_TIMEOVER("invalid.event.enroll.timeover"),
    INVALID_ENROLL_STATUS("invalid.event.enroll.status"),
    INVALID_ENROLL_REQUIRED("invalid.event.enroll.required"),
    
    // USER ID
    INVALID_USER_ID("invalid.invalid_userid"),
    MISSING_USER_ID("invalid.missing_userid"),

    // OPEN ID
    INVALID_OPENID("invalid.invalid_openid"),
    MISSING_OPENID("invalid.missing_openid"),
    INVALID_OPENID_PURPOSE("invalid.openid.purpose"),
    INVALID_OPENID_IDENTIFIER("invalid.openid.identifier"),

    // IMAGE ID
    INVALID_IMAGEID("invalid.image.id"),
    MISSING_IMAGEID("invalid.image.id.missing"),

    // CALENDAR
    INVALID_CALENDAR_ID("invalid.calendar.id"),
    MISSING_CALENDAR_ID("invalid.calendar.id.missing"),
    
    // SESSION
    INVALID_SESSION("invalid.invalid_session"),
    MISSING_SESSION("invalid.missing_session"),

    // SECURITY
    INVALID_SECURITY_CSRF("invalid.security.csrf"),
    
    // ATTENDANCE
    INVALID_ATTENDANCE_STATUS("invalid.invalid_attendance_status"),
    MISSING_ATTENDANCE_STATUS("invalid.missing_attendance_status"),
    
    // SEARCH
    INVALID_SEARCH_QUERY("invalid.invalid_search_query"),
    MISSING_SEARCH_QUERY("invalid.missing_search_query"),
    INVALID_SEARCH_CATEGORY("invalid.invalid_search_category"),
    MISSING_SEARCH_CATEGORY("invalid.missing_search_category"),
    INVALID_SEARCH_DEADLINE("invalid.invalid_search_deadline"),
    MISSING_SEARCH_DEADLINE("invalid.missing_search_deadline"),
    INVALID_SEARCH_ORDER("invalid.invalid_search_order"),
    MISSING_SEARCH_ORDER("invalid.missing_search_order"),
    INVALID_SEARCH_MAXNUM("invalid.invalid_search_max_num"),
    MISSING_SEARCH_MAXNUM("invalid.missing_search_max_num"),

    // COMMENT
    INVALID_COMMENT_ID("invalid.comment.id"),
    MISSING_COMMENT_ID("invalid.comment.id.missing"),
    INVALID_COMMENT_TOOLONG("invalid.comment.toolong"),
    COMMENT_REMOVAL_FORBIDDEN("invalid.comment.removal.forbidden", 403),
    
    // MESSAGE
    MISSING_MESSAGE("invalid.message.missing"),
    INVALID_MESSAGE_TOOMUCH("invalid.message.toomuch"),
    INVALID_MESSAGE_TOOLONG("invalid.message.toolong");
    
    // ----------------------------------------------------------------------
    private final String errorDescriptionId;
    private final int statusCode;

    private UserErrorCode(String errorReason) {
        this(errorReason, 400);
    }

    private UserErrorCode(String errorReason, int statusCode) {
        this.errorDescriptionId = errorReason;
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getReasonString() {
        return I18n.t(errorDescriptionId);
    }

}
