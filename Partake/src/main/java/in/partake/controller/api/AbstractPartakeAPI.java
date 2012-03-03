package in.partake.controller.api;

import in.partake.controller.base.AbstractPartakeController;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

public abstract class AbstractPartakeAPI extends AbstractPartakeController {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AbstractPartakeAPI.class);

    private InputStream stream;
    private int status;
    private Map<String, String> headers;

    protected AbstractPartakeAPI() {
        this.status = 200;
        this.headers = new HashMap<String, String>();
    }

    // ----------------------------------------------------------------------
    // 
    
    protected void addHeader(String key, String value) {
        headers.put(key, value);
    }
    
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

    // ----------------------------------------------------------------------
    // Rendering
    
    /**
     * JSON object をレスポンスとして返す。
     * @param obj
     * @return
     */
    protected String renderJSON(JSONObject obj) {
        assert obj != null;

        try {
            this.stream = new ByteArrayInputStream(obj.toString().getBytes("utf-8"));
            return "json";
        } catch (UnsupportedEncodingException e) {
            logger.fatal("This exception should not be thrown!", e);
            throw new RuntimeException(e);
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
    protected String renderError(ServerErrorCode errorCode) {
        return renderError(errorCode, null);
    }

    /**
     * <code>{ "result": "error", "reason": reason }</code> をレスポンスとして返す。
     * ステータスコードは 500 を返す。
     */
    protected String renderError(ServerErrorCode errorCode, Throwable e) {
        assert errorCode != null;
        
        final String reasonString = errorCode.toString() + ":" + errorCode.getReasonString(); 
        if (e != null) { logger.error(reasonString, e); }
        else { logger.error(reasonString); }

        JSONObject obj = new JSONObject();
        obj.put("result", "error");
        obj.put("reason", errorCode.getReasonString());
        this.status = 500;
        return renderJSON(obj);        
    }
    
    /**
     * <code>{ "result": "invalid", "reason": reason }</code> をレスポンスとして返す。
     * ステータスコードは 400 を返す。
     */
    protected String renderInvalid(UserErrorCode ec) {
        return renderInvalid(ec, null, null);
    }

    protected String renderInvalid(UserErrorCode ec, Throwable e) {
        return renderInvalid(ec, null, e);
    }

    protected String renderInvalid(UserErrorCode ec, JSONObject errorParams) {
        return renderInvalid(ec, errorParams, null);
    }

    protected String renderInvalid(UserErrorCode ec, JSONObject errorParams, Throwable e) {
        if (e != null)
            logger.info("renderInvalid", e);
        
        JSONObject obj = new JSONObject();
        obj.put("result", "invalid");
        obj.put("reason", ec.getReasonString());
        if (errorParams != null)
            obj.put("errorParameters", errorParams);
        
        this.status = 400;
        return renderJSON(obj);
    }
    
    protected String renderLoginRequired() {
        JSONObject obj = new JSONObject();
        obj.put("result", "auth");
        obj.put("reason", "login is required");

        this.status = 401;
        addHeader("WWW-Authenticate", "OAuth");
        return renderJSON(obj);
    }

    protected String renderForbidden() {
        JSONObject obj = new JSONObject();
        obj.put("result", "forbidden");
        obj.put("reason", "forbidden action");

        this.status = 403;
        return renderJSON(obj);
    }
    
    protected String renderForbidden(UserErrorCode ec) {
        JSONObject obj = new JSONObject();
        obj.put("result", "forbidden");
        obj.put("reason", ec.getReasonString());
        obj.put("errorCode", ec.toString());

        this.status = 403;
        return renderJSON(obj);
    }
    
    protected String renderNotFound() {
        JSONObject obj = new JSONObject();
        obj.put("result", "notfound");
        obj.put("reason", "not found");
        this.status = 404;
        return renderJSON(obj);
    }        
}
