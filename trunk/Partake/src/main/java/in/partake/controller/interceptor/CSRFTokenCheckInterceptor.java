package in.partake.controller.interceptor;

import in.partake.controller.PartakeActionSupport;
import in.partake.controller.PartakeInvalidResultException;
import in.partake.resource.Constants;
import in.partake.util.security.CSRFPrevention;

import com.opensymphony.xwork2.ActionInvocation;

public class CSRFTokenCheckInterceptor extends PartakeAbstractInterceptor {
    /** serial version UID */
    private static final long serialVersionUID = 1L;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        CSRFPrevention prevention = (CSRFPrevention) invocation.getInvocationContext().getSession().get(Constants.ATTR_CSRF_PREVENTION);
        if (prevention == null) { return PartakeActionSupport.ERROR; }
        
        String sessionToken = getParameter(Constants.ATTR_PARTAKE_TOKEN);
        String onetimeToken = getParameter(Constants.ATTR_PARTAKE_ONETIME_TOKEN);
        
        // invalid request
        if (sessionToken == null) {
            throw new PartakeInvalidResultException("セッショントークンがリクエストに含まれていません。");
        }

        synchronized (prevention) {
            if (!prevention.getSessionToken().equals(sessionToken)) {
                throw new PartakeInvalidResultException("リクエストに含まれるセッショントークンが一致しませんでした。");
            }
            
            // onetime token はある場合のみ利用される。
            if (onetimeToken != null && prevention.isConsumed(onetimeToken)) {
                return PartakeActionSupport.DUPLICATED;
            }
        
            prevention.consumeToken(onetimeToken);
        }

        return invocation.invoke();
    }
}