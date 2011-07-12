package in.partake.resource;

/**
 * UserErrorCode describes why the user request is invalid.
 * @author shinyak
 *
 */
public enum UserErrorCode {
    //
    UNKNOWN_USER_ERROR("invalid.unknown"),

    // EVENT ID
    INVALID_EVENT_ID("invalid.invalid_eventid"),
    MISSING_EVENT_ID("invalid.missing_eventid"),

    // USER ID
    INVALID_USER_ID("invalid.invalid_userid"),
    MISSING_USER_ID("invalid.missing_userid"),

    // OPEN ID
    INVALID_OPENID("invalid.invalid_openid"),
    MISSING_OPENID("invalid.missing_openid"),

    // SESSION
    MISSING_SESSION("invalid.missing_session"),

    MISSING_ATTENDANCE_STATUS("invalid.missing_attendance_status");

    // ----------------------------------------------------------------------
    private String errorDescriptionId;

    private UserErrorCode(String errorReason) {
        this.errorDescriptionId = errorReason;
    }

    public String getReasonString() {
        return I18n.t(errorDescriptionId);
    }

}
