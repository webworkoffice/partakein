package in.partake.servlet;

import in.partake.util.security.CSRFPrevention;

/**
 * PartakeSession is a type safe session object.
 * @author shinyak
 *
 */
public class PartakeSession {
    private CSRFPrevention csrfPrevention;
    
    public PartakeSession() {
        // Do nothing for while.
    }
    
    public void setCSRFPrevention(CSRFPrevention prevention) {
        this.csrfPrevention = prevention;
    }

    public CSRFPrevention getCSRFPrevention() {
        return this.csrfPrevention;
    }    
}
