package in.partake.controller.interceptor;

import in.partake.controller.PartakeInvalidResultException;
import in.partake.controller.PartakeResultException;
import in.partake.resource.Constants;
import in.partake.servlet.PartakeSession;

import java.util.Map;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public abstract class PartakeAbstractInterceptor extends AbstractInterceptor {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(PartakeAbstractInterceptor.class);

    protected void returnInvalid(String why) throws PartakeResultException {
        throw new PartakeInvalidResultException(why);
    }
    
    // 最初の parameter を取る utility function.
    // TODO: これ PartakeActionSupport と同一なので一元化したい
    protected String getParameter(String key) {
        // TODO: ParameterAware で渡されるときには値が入ってないのに、後で値が入ってくる。
        // どうも defaultStack の途中で処理されている？　ちょっと分からないので、ここで毎回 getParameters を取ることにする。
        // あとで気がついたら直す。
        Map<String, Object> params = ActionContext.getContext().getParameters();
        Object param = params.get(key);
        if (param == null) { return null; }
        if (param instanceof String) {
            return (String)param;
        } else if (param instanceof String[]) {
            String[] strs = (String[])param;
            if (strs.length == 0) { return null; }
            else { return strs[0]; }
        } else {
            logger.warn("shouldn't happen.");
            return null;
        }
    }
    
    protected PartakeSession getSession(ActionInvocation invocation) {
         Map<String, Object> session = invocation.getInvocationContext().getSession();
         assert session != null;
         
         return (PartakeSession) session.get(Constants.ATTR_PARTAKE_SESSION);
    }

}
