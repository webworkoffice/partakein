package in.partake.controller.interceptor;

import in.partake.controller.DeprecatedPartakeActionSupport;
import in.partake.controller.base.PartakeInvalidResultException;
import in.partake.resource.Constants;
import in.partake.resource.UserErrorCode;
import in.partake.session.CSRFPrevention;
import in.partake.session.PartakeSession;

import com.opensymphony.xwork2.ActionInvocation;

public class CSRFTokenCheckInterceptor extends PartakeAbstractInterceptor {
    /** serial version UID */
    private static final long serialVersionUID = 1L;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        PartakeSession session = getSession(invocation);
        if (session == null) { return DeprecatedPartakeActionSupport.ERROR; }
        
        CSRFPrevention prevention = session.getCSRFPrevention(); 
        if (prevention == null) { return DeprecatedPartakeActionSupport.ERROR; }
        
        String sessionToken = getParameter(Constants.ATTR_PARTAKE_API_SESSION_TOKEN);
        
        // invalid request
        if (sessionToken == null) {
            throw new PartakeInvalidResultException(UserErrorCode.MISSING_SESSION);
        }

        if (!prevention.getSessionToken().equals(sessionToken)) {
            throw new PartakeInvalidResultException(UserErrorCode.INVALID_SESSION);
        }            

        return invocation.invoke();
    }
}