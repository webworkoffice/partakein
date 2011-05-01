package in.partake.controller.interceptor;

import in.partake.resource.Constants;
import in.partake.servlet.PartakeSession;
import in.partake.util.security.CSRFPrevention;

import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class CSRFTokenInterceptor extends AbstractInterceptor {
    /** serial version UID */
    private static final long serialVersionUID = 1L;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> session = invocation.getInvocationContext().getSession();

        CSRFPrevention prevention = getCurrentCSRFPrevention(session);
        if (prevention == null) { prevention = new CSRFPrevention(); }        
        setCSRFPrevention(session, prevention);
        
        return invocation.invoke();
    }
    
    private CSRFPrevention getCurrentCSRFPrevention(Map<String, Object> session) {
        if (session == null) { return null; }
        
        // get 
        if (session.get(Constants.ATTR_CSRF_PREVENTION) != null) {
            CSRFPrevention prevention =  (CSRFPrevention) session.get(Constants.ATTR_CSRF_PREVENTION);
            if (prevention != null) { return prevention; }
        }
        if (session.get(Constants.ATTR_PARTAKE_SESSION) != null) {
            PartakeSession partakeSession = (PartakeSession) session.get(Constants.ATTR_PARTAKE_SESSION);
            if (partakeSession == null) { return null; }
            CSRFPrevention prevention = partakeSession.getCSRFPrevention();
            if (prevention != null) { return prevention; }
        }
        
        return null;
    }
    
    private void setCSRFPrevention(Map<String, Object> session, CSRFPrevention prevention) {
        if (session == null) { return; }
        
        session.put(Constants.ATTR_CSRF_PREVENTION, prevention);
        PartakeSession partakeSession = (PartakeSession) session.get(Constants.ATTR_PARTAKE_SESSION);
        if (partakeSession != null) {
            partakeSession.setCSRFPrevention(prevention);
        }        
    }
}