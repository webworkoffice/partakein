package in.partake.interceptor;

import in.partake.controller.PartakeActionSupport;
import in.partake.model.UserEx;
import in.partake.resource.Constants;

import java.util.Map;

import org.apache.struts2.ServletActionContext;


import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class LoginRequiredInterceptor extends AbstractInterceptor {
	
	/** */
	private static final long serialVersionUID = 3230597802643457900L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		Map<String, Object> session = invocation.getInvocationContext().getSession();
		UserEx user = (UserEx)session.get(Constants.ATTR_USER);
		
		if (user == null) {
			Object action = invocation.getAction();
			if (action instanceof PartakeActionSupport) {
				PartakeActionSupport partake = (PartakeActionSupport)action;
				partake.setRedirectURL(ServletActionContext.getRequest().getRequestURL().toString());
			} else {
				System.out.println("action is not PartakeActionSupport. something wrong.");
			}

			return PartakeActionSupport.LOGIN;
		} else {
			return invocation.invoke();
		}
	}
	
}
