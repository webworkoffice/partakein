package in.partake.controller.api;

import in.partake.controller.PartakeActionSupport;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class PartakeAPIActionSupport extends PartakeActionSupport {
    private static final long serialVersionUID = 1L;

    protected InputStream stream;
    protected int status;
    protected Map<String, String> headers;
        
    public PartakeAPIActionSupport() {
        this.status = 200;
        this.headers = new HashMap<String, String>();
    }
    
    // ----------------------------------------------------------------------
    
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }
    
    // ----------------------------------------------------------------------
    
    /**
     * JSON object をレスポンスとして返す。
     * @param obj
     * @return
     */
    public String renderJSON(JSONObject obj) {
        assert obj != null;
        
        try {
            this.stream = new ByteArrayInputStream(obj.toString().getBytes("utf-8"));
            return "json";
        } catch (UnsupportedEncodingException e) {
            return ERROR;
        }      
    }
    
    /**
     * <code>{ "result": "ok"; }</code> をレスポンスとして返す。
     * @return
     */
    public String renderOK() {
        JSONObject obj = new JSONObject();
        obj.put("result", "ok");
        return renderJSON(obj);
    }
    
    /**
     * <code>{ "result": "error"; "reason": reason }</code> をレスポンスとして返す。
     */
    public String renderError(String reason) {
        JSONObject obj = new JSONObject();
        obj.put("result", "error");
        this.status = 500;
        return renderJSON(obj);
    }
    
    @Override
    protected String renderLoginRequired() {
        JSONObject obj = new JSONObject();
        obj.put("result", "auth");

        this.status = 401;
        addHeader("WWW-Authenticate", "OAuth");
        return renderJSON(obj);
    }
    
    // ----------------------------------------------------------------------
    // 
    
    /** return input stream. */
    public InputStream getInputStream() {
        return stream;
    }
    
    public String getContentType() {
        return "text/json";
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public Map<String, String> getHeaders() {
        return this.headers;
    }
}
