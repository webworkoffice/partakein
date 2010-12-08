package in.partake.interceptor;

import in.partake.controller.PartakeResultException;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class PartakeExceptionInterceptor extends AbstractInterceptor {
    /** */
    private static final long serialVersionUID = 1L;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        try {
            return invocation.invoke();
        } catch (PartakeResultException e) {
            return e.getResult();
        }
    }
}
