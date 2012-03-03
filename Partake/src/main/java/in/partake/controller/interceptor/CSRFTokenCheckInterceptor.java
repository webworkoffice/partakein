package in.partake.controller.interceptor;

import in.partake.controller.PartakeActionSupport;
import in.partake.controller.PartakeInvalidResultException;
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
        if (session == null) { return PartakeActionSupport.ERROR; }
        
        CSRFPrevention prevention = session.getCSRFPrevention(); 
        if (prevention == null) { return PartakeActionSupport.ERROR; }
        
        String sessionToken = getParameter(Constants.ATTR_PARTAKE_TOKEN);
        String onetimeToken = getParameter(Constants.ATTR_PARTAKE_ONETIME_TOKEN);
        
        // invalid request
        if (sessionToken == null) {
            throw new PartakeInvalidResultException(UserErrorCode.MISSING_SESSION);
        }

        synchronized (prevention) {
            if (!prevention.getSessionToken().equals(sessionToken)) {
                throw new PartakeInvalidResultException(UserErrorCode.INVALID_SESSION);
            }
            
            // onetime token はある場合のみ利用される。
            if (onetimeToken != null && prevention.isConsumed(onetimeToken)) {
                return PartakeActionSupport.INVALID;
            }
        
            prevention.consumeToken(onetimeToken);
        }

        return invocation.invoke();
    }
}