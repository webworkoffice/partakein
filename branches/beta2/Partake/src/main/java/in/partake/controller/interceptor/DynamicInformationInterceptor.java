package in.partake.controller.interceptor;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import in.partake.controller.DeprecatedPartakeActionSupport;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.resource.Constants;
import in.partake.session.PartakeSession;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

// TODO: rename to more appropriate name.
public class DynamicInformationInterceptor extends AbstractInterceptor {

    /** */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(DynamicInformationInterceptor.class);


    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        final Object action = invocation.getAction();
        final ActionContext context = invocation.getInvocationContext();

        String currentURL = ServletActionContext.getRequest().getRequestURL().toString();

        // TODO: ATTR_CURRENT_URL should be moved to PartakePageAttribute.
        // TODO: ATTR_CURRENT_URL should be OBSOLETE later.
        if (action instanceof AbstractPartakeAction) {
            AbstractPartakeAction pas = (AbstractPartakeAction) action;
            pas.setCurrentURL(currentURL);
            ServletActionContext.getRequest().setAttribute(Constants.ATTR_CURRENT_URL, currentURL);
        } else {
            logger.warn("action is not extended from PartakeActionSupport");
        }

        // create PartakeSession
        if (context.getSession() != null && !context.getSession().containsKey(Constants.ATTR_PARTAKE_SESSION)) {
            PartakeSession partakeSession = PartakeSession.createInitialPartakeSession();
            context.getSession().put(Constants.ATTR_PARTAKE_SESSION, partakeSession);
        }

        logger.info("processing... " + currentURL);
        long begin = System.currentTimeMillis();
        try {
            return invocation.invoke();
        } finally {
            long end = System.currentTimeMillis();
            logger.info(currentURL + " took "+ (end - begin) + "[msec] to process.");		    
        }
    }
}
