package in.partake.interceptor;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import in.partake.controller.PartakeActionSupport;
import in.partake.resource.Constants;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class DynamicInformationInterceptor extends AbstractInterceptor {
	
	/** */
	private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(DynamicInformationInterceptor.class);

    
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		Object action = invocation.getAction();
		String currentURL = ServletActionContext.getRequest().getRequestURL().toString();
		
		if (action instanceof PartakeActionSupport) {
			PartakeActionSupport pas = (PartakeActionSupport) action;
			pas.setCurrentURL(currentURL);
			ServletActionContext.getRequest().setAttribute(Constants.ATTR_CURRENT_URL, currentURL);
		} else {
			logger.warn("action is not extended from PartakeActionSupport");
		}
		
		logger.info("processing... " + currentURL);
		long begin = System.currentTimeMillis();
		try {
    		return invocation.invoke(); 
    	} catch (Exception e) {
		    logger.error(currentURL, e);
		    throw e;
		} finally {
            long end = System.currentTimeMillis();
            logger.info(currentURL + " took "+ (end - begin) + "[msec] to process.");		    
		}
	}
}
