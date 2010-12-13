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
		if (action instanceof PartakeActionSupport) {
			PartakeActionSupport pas = (PartakeActionSupport) action;
			String currentURL = ServletActionContext.getRequest().getRequestURL().toString(); 
			pas.setCurrentURL(currentURL);
			ServletActionContext.getRequest().setAttribute(Constants.ATTR_CURRENT_URL, currentURL);
		} else {
			logger.warn("action is not extended from PartakeActionSupport");
		}
		
		return invocation.invoke();
	}
}
