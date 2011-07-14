package in.partake.servlet;

import in.partake.util.security.CSRFPrevention;

/**
 * PartakeSession is a type safe session object.
 * @author shinyak
 *
 */
public class PartakeSession {
    private CSRFPrevention csrfPrevention;
    
    private PartakeSession(CSRFPrevention prevention) {
        this.csrfPrevention = prevention;
    }
    
    public static PartakeSession createInitialPartakeSession() {
        return new PartakeSession(new CSRFPrevention());
    }
    
    public CSRFPrevention getCSRFPrevention() {
        return this.csrfPrevention;
    }
}
