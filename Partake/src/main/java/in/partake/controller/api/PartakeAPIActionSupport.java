package in.partake.controller.api;

import in.partake.controller.PartakeActionSupport;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.I18n;
import in.partake.resource.UserErrorCode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class PartakeAPIActionSupport extends PartakeActionSupport {
    private static final long serialVersionUID = 1L;

    private InputStream stream;
    private int status;
    private Map<String, String> headers;
        
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
     * <code>{ "result": "ok" }</code> をレスポンスとして返す。
     * with status code 200.
     * @return
     */
    protected String renderOK() {
        return renderOK(new JSONObject());
    }
    
    /**
     * obj に result: ok を追加して返す。obj に result が既に含まれていれば RuntimeException を投げる。
     * @param obj
     * @return
     */
    protected String renderOK(JSONObject obj) {
        if (obj.containsKey("result")) {
            throw new RuntimeException("obj should not contain result");
        }
        obj.put("result", "ok");
        return renderJSON(obj);
    }
    
    /**
     * <code>{ "result": "error", "reason": reason }</code> をレスポンスとして返す。
     * ステータスコードは 500 を返す。
     */
    @Deprecated
    protected String renderError(String reason) {
        JSONObject obj = new JSONObject();
        obj.put("result", "error");
        obj.put("reason", reason); 
        this.status = 500;
        return renderJSON(obj);
    }

    /**
     * renderError(String) と同じだが、DB エラーの場合はこちらを使うこと。
     * @return
     */
    @Deprecated
    protected String renderDBError() {
        JSONObject obj = new JSONObject();
        obj.put("result", "error");
        obj.put("reason", I18n.t(I18n.DATABASE_ERROR)); 
        this.status = 500;
        return renderJSON(obj);
    }

    /**
     * <code>{ "result": "error", "reason": reason }</code> をレスポンスとして返す。
     * ステータスコードは 500 を返す。
     */
    protected String renderError(ServerErrorCode errorCode) {
        assert errorCode != null;
        
        JSONObject obj = new JSONObject();
        obj.put("result", "error");
        obj.put("reason", errorCode.getReasonString()); 
        this.status = 500;
        return renderJSON(obj);        
    }
    
    /**
     * <code>{ "result": "invalid", "reason": rason }</code> をレスポンスとして返す。
     * ステータスコードは 400 を返す。
     * 
     * Use renderInvalid(UserErrorCode) instead. 
     */
    @Deprecated
    protected String renderInvalid(String reason) {
        JSONObject obj = new JSONObject();
        obj.put("result", "invalid");
        obj.put("reason", reason); 
        this.status = 400;
        return renderJSON(obj);        
    }
    
    /**
     * <code>{ "result": "invalid", "reason": rason }</code> をレスポンスとして返す。
     * ステータスコードは 400 を返す。
     */
    protected String renderInvalid(UserErrorCode errorCode) {
        JSONObject obj = new JSONObject();
        obj.put("result", "invalid");
        obj.put("reason", errorCode.getReasonString()); 
        this.status = 400;
        return renderJSON(obj);                
    }    
    
    @Override
    protected String renderLoginRequired() {
        JSONObject obj = new JSONObject();
        obj.put("result", "auth");
        obj.put("reason", "login is required");

        this.status = 401;
        addHeader("WWW-Authenticate", "OAuth");
        return renderJSON(obj);
    }

    @Override
    protected String renderForbidden() {
        JSONObject obj = new JSONObject();
        obj.put("result", "forbidden");
        obj.put("reason", "forbidden action");

        this.status = 403;
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
