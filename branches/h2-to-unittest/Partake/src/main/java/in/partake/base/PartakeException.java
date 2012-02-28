package in.partake.base;

import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

/**
 * An Exception which contains ServerErrorCode or UserErrorCode.
 * Either ServerErrorCode or UserErrorCode should be included.
 *
 * @author shinyak
 */
public class PartakeException extends Exception {
    private static final long serialVersionUID = 1L;

    private ServerErrorCode serverErrorCode;
    private UserErrorCode userErrorCode;
    
    public PartakeException(ServerErrorCode ec) {
        super(ec.getReasonString());
        this.serverErrorCode = ec;
    }

    public PartakeException(ServerErrorCode ec, Throwable t) {
        super(ec.getReasonString(), t);
        this.serverErrorCode = ec;
    }
    
    public PartakeException(UserErrorCode ec) {
        super(ec.getReasonString());
        this.userErrorCode = ec;
    }

    public PartakeException(UserErrorCode ec, Throwable t) {
        super(ec.getReasonString(), t);
        this.userErrorCode = ec;
    }

    public boolean isServerError() {
        return serverErrorCode != null;
    }
    
    public ServerErrorCode getServerErrorCode() {
        return serverErrorCode;
    }
    
    public boolean isUserError() {
        return userErrorCode != null;
    }
    
    public UserErrorCode getUserErrorCode() {
        return userErrorCode;
    }
    
    public int getStatusCode() {
        if (serverErrorCode != null)
            return serverErrorCode.getStatusCode();
        else
            return userErrorCode.getStatusCode();
    }
}
