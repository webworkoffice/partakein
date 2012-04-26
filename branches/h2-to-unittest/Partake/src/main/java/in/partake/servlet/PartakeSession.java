package in.partake.servlet;

import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.util.security.CSRFPrevention;

/**
 * PartakeSession is a type safe session object.
 * @author shinyak
 *
 */
public class PartakeSession {
    private CSRFPrevention csrfPrevention;
    private ServerErrorCode lastServerError;
    private UserErrorCode lastUserError;
    
    private PartakeSession(CSRFPrevention prevention) {
        this.csrfPrevention = prevention;
    }
    
    public static PartakeSession createInitialPartakeSession() {
        return new PartakeSession(new CSRFPrevention());
    }
    
    public CSRFPrevention getCSRFPrevention() {
        return this.csrfPrevention;
    }
    
    public void setLastServerError(ServerErrorCode ec) {
        this.lastServerError = ec;
    }
    
    public void setLastUserError(UserErrorCode ec) {
        this.lastUserError = ec;
    }
    
    public boolean hasServerErrorCode() {
        return this.lastServerError != null;
    }
    
    public boolean hasUserErrorCode() {
        return this.lastUserError != null;
    }
    
    public ServerErrorCode getLastServerError() {
        return this.lastServerError;        
    }
    
    public UserErrorCode getLastUserErrorCode() {
        return this.lastUserError;
    }
}