package in.partake.interceptor;

import org.apache.struts2.ServletActionContext;

import in.partake.controller.PartakeActionSupport;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class DynamicInformationInterceptor extends AbstractInterceptor {
	
	/** */
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		Object action = invocation.getAction();
		if (action instanceof PartakeActionSupport) {
			PartakeActionSupport pas = (PartakeActionSupport) action;			
			pas.setCurrentURL(ServletActionContext.getRequest().getRequestURL().toString());
		}
		
		return invocation.invoke();
	}
}
