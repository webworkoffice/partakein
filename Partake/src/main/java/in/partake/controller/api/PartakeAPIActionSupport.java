package in.partake.controller.api;

import in.partake.controller.PartakeActionSupport;
import in.partake.resource.Constants;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

public class PartakeAPIActionSupport extends PartakeActionSupport {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(PartakeAPIActionSupport.class);
    
    private InputStream stream;
    private int status;
    private Map<String, String> headers;

    public PartakeAPIActionSupport() {
        this.status = 200;
        this.headers = new HashMap<String, String>();
    }

    // ----------------------------------------------------------------------

    protected void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * parameter から session token を取得し、チェックします。
     * @return true if valid token. false otherwise.
     */
    protected boolean checkSessionToken() {
        String token = getParameter(Constants.ATTR_PARTAKE_API_SESSION_TOKEN);
        return getPartakeSession().getCSRFPrevention().isValidSessionToken(token); 
    }

    // ----------------------------------------------------------------------

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
        logger.error(reason);
        
        JSONObject obj = new JSONObject();
        obj.put("result", "error");
        obj.put("reason", reason);
        this.status = 500;
        return renderJSON(obj);
    }

    /**
     * @deprected Use renderError(ServerError.DB_ERROR) instead.
     * @return
     */
    @Deprecated
    protected String renderDBError() {
        logger.error(ServerErrorCode.DB_ERROR.getReasonString());
        
        JSONObject obj = new JSONObject();
        obj.put("result", "error");
        obj.put("reason", ServerErrorCode.DB_ERROR.getReasonString());
        this.status = 500;
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
    @Override
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
    @Override
    public InputStream getInputStream() {
        return stream;
    }

    @Override
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
