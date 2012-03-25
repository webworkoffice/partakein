package in.partake.controller.base;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.resource.Constants;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.session.CSRFPrevention;
import in.partake.session.PartakeSession;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;

// TODO: We don't need to extends ActionSupport now.
public abstract class AbstractPartakeController implements SessionAware, RequestAware, ServletRequestAware, Serializable {
    /** */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AbstractPartakeController.class);

    /** return code in case parameter is invalid. (ユーザーのリクエストがおかしい場合) */
    protected static final String INVALID = "invalid"; //$NON-NLS-1$
    /** return code in case redirection */
    protected static final String REDIRECT = "redirect"; //$NON-NLS-1$
    /** return code in case the required resource is not found.*/
    protected static final String NOT_FOUND = "notfound"; //$NON-NLS-1$
    /** return code in case the operation is prohibited. */
    protected static final String PROHIBITED = "prohibited"; //$NON-NLS-1$

    // 様々なところで使うので、redirectURL を定義しておく。
    // あんまりよろしくないが、loginRequired でこれを使って、かつ login が必要なところは色色あるのでベースとして定義する
    protected String redirectURL;
    protected String currentURL;

    protected String location;

    // These are used for returning stream.
    protected InputStream stream;
    protected String contentType;
    protected String contentDisposition;

    // somethingAware な人たち向け
    protected Map<String, Object> session = null;
    protected Map<String, Object> attributes = null;
    protected HttpServletRequest request = null;

    // ----------------------------------------------------------------------
    // Execute

    public final String execute() {
        try {
            return doExecute();
        } catch (DAOException e) {
            return renderError(ServerErrorCode.DB_ERROR, e);
        } catch (PartakeException e) {
            return renderException(e);
        } catch (RuntimeException e) {
            return renderError(ServerErrorCode.UNKNOWN_ERROR, e);
        } catch (Exception e) {
            return renderError(ServerErrorCode.UNKNOWN_ERROR, e);
        }
    }

    protected abstract String doExecute() throws PartakeException, DAOException;

    // ----------------------------------------------------------------------
    // Render

    protected abstract String renderInvalid(UserErrorCode ec, Throwable e);
    protected abstract String renderError(ServerErrorCode ec, Throwable e);
    protected abstract String renderLoginRequired();
    protected abstract String renderForbidden();
    protected abstract String renderNotFound();

    protected String renderException(PartakeException e) {
        if (e.getStatusCode() == 401)
            return renderLoginRequired();
        if (e.getStatusCode() == 403)
            return renderForbidden();
        if (e.getStatusCode() == 404)
            return renderNotFound();

        if (e.isUserError())
            return renderInvalid(e.getUserErrorCode(), e.getCause());

        assert e.isServerError();
        return renderError(e.getServerErrorCode(), e.getCause());
    }

    // ----------------------------------------------------------------------
    // Session

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    @Override
    public void setRequest(Map<String, Object> attributes) {
        this.attributes = attributes;

        // page attribute に、自分自身を付け加えておく。
        if (this.attributes != null)
            this.attributes.put(Constants.ATTR_ACTION, this);
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    // ----------------------------------------------------------------------
    // Parameter

    // 最初の parameter を取る utility function.
    protected String getParameter(String key) {
        // TODO: ParameterAware で渡されるときには値が入ってないのに、後で値が入ってくる。
        // どうも defaultStack の途中で処理されている？　ちょっと分からないので、ここで毎回 getParameters を取ることにする。
        // あとで気がついたら直す。
        Map<String, Object> params = ActionContext.getContext().getParameters();
        Object param = params.get(key);
        if (param == null) { return null; }
        if (param instanceof String) {
            return (String)param;
        } else if (param instanceof String[]) {
            String[] strs = (String[])param;
            if (strs.length == 0) { return null; }
            else { return strs[0]; }
        } else {
            logger.warn("shouldn't happen.");
            return null;
        }
    }

    /**
     * Gets boolean parameter. If parameter does not exist, null will be returned.
     * @param key
     * @param defaultValue
     * @return
     */
    protected Boolean getBooleanParameter(String key) {
        String value = getParameter(key);
        if (value == null)
            return null;

        return Util.parseBooleanParameter(value);
    }

    protected boolean optBooleanParameter(String key, boolean defaultValue) {
        Boolean value = getBooleanParameter(key);
        if (value != null)
            return value;
        return defaultValue;
    }

    protected Integer getIntegerParameter(String key) {
        String value = getParameter(key);
        if (value == null)
            return null;

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected int optIntegerParameter(String key, int defaultValue) {
        Integer value = getIntegerParameter(key);
        if (value != null)
            return value;
        return defaultValue;
    }

    protected Long getLongParameter(String key) {
        String value = getParameter(key);
        if (value == null)
            return null;

        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Date getDateParameter(String key) {
        String value = getParameter(key);
        if (value == null)
            return null;

        Date date = TimeUtil.parseForEvent(value);
        if (date != null)
            return date;

        // Try parse it as long.
        try {
            long time = Long.valueOf(value);
            return new Date(time);
        } catch (NumberFormatException e) {
            // Do nothing.
        }

        return null;
    }

    protected String getValidIdParameter(String key, UserErrorCode missing, UserErrorCode invalid) throws PartakeException {
        String id = getParameter(key);
        if (id == null)
            throw new PartakeException(missing);
        if (!Util.isUUID(id))
            throw new PartakeException(invalid);

        return id;
    }

    protected String optValidIdParameter(String key, UserErrorCode invalid, String defaultValue) throws PartakeException {
        String id = getParameter(key);
        if (id == null)
            return defaultValue;
        if (!Util.isUUID(id))
            throw new PartakeException(invalid);

        return id;
    }

    protected String getValidUserIdParameter(UserErrorCode missing, UserErrorCode invalid) throws PartakeException {
        return getValidIdParameter("userId", missing, invalid);
    }

    protected String getValidUserIdParameter() throws PartakeException {
        return getValidIdParameter("userId", UserErrorCode.MISSING_USER_ID, UserErrorCode.INVALID_USER_ID);
    }

    protected String getValidEventIdParameter() throws PartakeException {
        return getValidIdParameter("eventId", UserErrorCode.MISSING_EVENT_ID, UserErrorCode.INVALID_EVENT_ID);
    }

    protected String getValidEventIdParameter(UserErrorCode missing, UserErrorCode invalid) throws PartakeException {
        return getValidIdParameter("eventId", missing, invalid);
    }

    protected String getValidImageIdParameter() throws PartakeException {
        return getValidIdParameter("imageId", UserErrorCode.MISSING_IMAGEID, UserErrorCode.INVALID_IMAGEID);
    }

    protected String getValidCommentIdParameter() throws PartakeException {
        return getValidIdParameter("commentId", UserErrorCode.MISSING_COMMENT_ID, UserErrorCode.INVALID_COMMENT_ID);
    }

    protected void ensureValidSessionToken() throws PartakeException {
        if (!checkCSRFToken())
            throw new PartakeException(UserErrorCode.INVALID_SECURITY_CSRF);
    }

    /**
     * take multiple parameters. If there is a single parameter, a new array will be created to return.
     * @param key
     * @return
     */
    protected String[] getParameters(String key) {
        Map<String, Object> params = ActionContext.getContext().getParameters();
        if (params == null) { return null; }

        Object param = params.get(key);
        if (param == null) { return null; }
        if (param instanceof String) {
            return new String[]{ (String) param };
        }
        if (param instanceof String[]) {
            String[] strs = (String[])param;
            if (strs.length == 0) { return null; }
            return strs;
        }

        logger.warn(String.format(
                "Type of the parameter(%s) is not expected one. The given key is '%s'.",
                param.getClass().toString(),
                key));
        return null;
    }

    public String getLocation() {
        return location;
    }

    public void setRedirectURL(String url) {
        this.redirectURL = url;
    }

    public String getRedirectURL() {
        return this.redirectURL;
    }

    public void setCurrentURL(String url) {
        this.currentURL = url;
    }

    public String getCurrentURL() {
        return this.currentURL;
    }

    public PartakeSession getPartakeSession() {
        if (session == null) { return null; }
        return (PartakeSession) session.get(Constants.ATTR_PARTAKE_SESSION);
    }

    // ----------------------------------------------------------------------
    // Utility function

    /**
     * get logged in user. If a user is not logged in, null is returned.
     * @return the logged in user. null if a user is not logged in.
     */
    protected UserEx getLoginUser() {
        if (session == null) { return null; }
        return (UserEx) session.get(Constants.ATTR_USER);
    }

    protected UserEx ensureLogin() throws PartakeException {
        UserEx user = getLoginUser();
        if (user == null)
            throw new PartakeException(UserErrorCode.INVALID_LOGIN_REQUIRED);

        return user;
    }

    protected UserEx ensureAdmin() throws PartakeException {
        UserEx user = ensureLogin();
        if (!user.isAdministrator())
            throw new PartakeException(UserErrorCode.INVALID_PROHIBITED);

        return user;
    }

    // ----------------------------------------------------------------------
    // CSRF

    private boolean checkCSRFToken() {
        PartakeSession session = getPartakeSession();
        if (session == null)
            return false;

        CSRFPrevention prevention = session.getCSRFPrevention();
        if (prevention == null)
            return false;

        String sessionToken = getParameter(Constants.ATTR_PARTAKE_SESSION);
        if (sessionToken == null)
            return false;

        return prevention.isValidSessionToken(sessionToken);
    }
}
