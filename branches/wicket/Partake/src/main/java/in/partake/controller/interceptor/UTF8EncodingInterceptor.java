package in.partake.controller.interceptor;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class UTF8EncodingInterceptor extends AbstractInterceptor {
	/** serial version UID */
    private static final long serialVersionUID = 1L;

    @Override
	public String intercept(ActionInvocation action) throws Exception {
		ServletActionContext.getRequest().setCharacterEncoding("utf-8");
		return action.invoke();
	}
}
