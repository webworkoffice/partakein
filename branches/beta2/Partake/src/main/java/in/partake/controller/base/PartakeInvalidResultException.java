package in.partake.controller.base;

import in.partake.resource.UserErrorCode;

/**
 * Controller can throw this exception to return a result.
 * @author shinyak
 */
@Deprecated
public class PartakeInvalidResultException extends PartakeResultException {
    /** */
    private static final long serialVersionUID = 1L;
    private UserErrorCode errorCode;
    private String description;
    
    // use PartakeInvalidResultException(UserErrorCode) instead
    @Deprecated
    public PartakeInvalidResultException(String description) {
        super("invalid");
        this.description = description;
        this.errorCode = UserErrorCode.UNKNOWN_USER_ERROR;
    }
    
    public PartakeInvalidResultException(UserErrorCode userErrorCode) {
        super("invalid");
        this.errorCode = userErrorCode;
        this.description = userErrorCode.getReasonString();
    }    
    
    public UserErrorCode getErrorCode() {
        return errorCode;
    }
    
    public String getDescription() {
        return description;
    }
}
