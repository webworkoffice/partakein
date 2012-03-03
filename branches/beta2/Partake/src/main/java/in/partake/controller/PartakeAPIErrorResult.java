package in.partake.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.sf.json.JSONObject;

import org.apache.struts2.dispatcher.HttpHeaderResult;
import org.apache.struts2.dispatcher.StreamResult;

import com.opensymphony.xwork2.ActionInvocation;

public class PartakeAPIErrorResult extends StreamResult {
    private static final long serialVersionUID = 1L;
    private HttpHeaderResult headerResult;
    
    public PartakeAPIErrorResult() {
        super(getResult());
        this.headerResult = new HttpHeaderResult();
    }

    public void execute(ActionInvocation invocation) throws Exception {
        headerResult.setError(500);
        headerResult.execute(invocation);
        
        super.execute(invocation);
    }

    // ----------------------------------------------------------------------
    
    private static InputStream getResult() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("result", "error");
            obj.put("reason", "unknown error"); 
            return new ByteArrayInputStream(obj.toString().getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            assert false; // assert not reached.
            throw new RuntimeException(e);
        }
    }
}
