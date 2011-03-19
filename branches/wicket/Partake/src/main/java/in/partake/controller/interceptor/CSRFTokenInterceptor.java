package in.partake.controller.interceptor;

import in.partake.resource.Constants;
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

        // token が設定されていなければ設定する
        if (session != null && session.get(Constants.ATTR_CSRF_PREVENTION) == null) {
            session.put(Constants.ATTR_CSRF_PREVENTION, new CSRFPrevention());
        }

        return invocation.invoke();
    }
}