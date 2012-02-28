package in.partake.controller;

import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.struts2.dispatcher.HttpHeaderResult;
import org.apache.struts2.dispatcher.StreamResult;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;

public class PartakeStreamResult extends StreamResult {
    private static final long serialVersionUID = 1L;
    private HttpHeaderResult headerResult;
    private Map<String, String> additionalHeaders;
    
    public PartakeStreamResult() {
        this.headerResult = new HttpHeaderResult();
    }

    public PartakeStreamResult(InputStream in) {
        super(in);
        this.headerResult = new HttpHeaderResult();
    }

    public void execute(ActionInvocation invocation) throws Exception {
        resolveParameters(invocation);
        
        if (additionalHeaders != null) {
            for (Entry<String, String> entry : additionalHeaders.entrySet()) {
                headerResult.addHeader(entry.getKey(), entry.getValue());
            }
        }
        
        headerResult.execute(invocation);
        super.execute(invocation);
    }

    @SuppressWarnings("unchecked")
    private void resolveParameters(ActionInvocation invocation) {
        ValueStack stack = ActionContext.getContext().getValueStack(); 
        
        String status = stack.findString("status");
        if (status != null) {
            setStatus(Integer.parseInt(status));
        }

        Object headers = stack.findValue("headers");
        if (headers instanceof Map) {
            setAdditionalHeaders((Map<String, String>) headers);
        }
    }
    
    // ----------------------------------------------------------------------
    
    public void setStatus(int status) {
        // We don't call headerResult.setError(status) here, because setError will send tomcat's error page instead of our JSON result. 
        headerResult.setStatus(status);
    }
    
    public void setAdditionalHeaders(Map<String, String> additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
    }
}
