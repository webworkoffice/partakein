package in.partake.controller.interceptor;

import in.partake.controller.PartakeActionSupport;
import in.partake.model.UserEx;
import in.partake.resource.Constants;

import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AdminRequiredInterceptor extends AbstractInterceptor {
	
	/** */
	private static final long serialVersionUID = 3230597802643457900L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		Map<String, Object> session = invocation.getInvocationContext().getSession();

		UserEx user = (UserEx) session.get(Constants.ATTR_USER);
		if (user == null || !user.isAdministrator()) {
			return PartakeActionSupport.PROHIBITED;
		} else {
		    return invocation.invoke();
		}		 
	}
	
}
