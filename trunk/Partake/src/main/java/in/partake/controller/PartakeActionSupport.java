package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.resource.Constants;
import in.partake.resource.I18n;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class PartakeActionSupport extends ActionSupport implements SessionAware, RequestAware, ServletRequestAware {
	/** */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PartakeActionSupport.class);

    // TODO: 
    //   INVALID とか見せないようにして、return renderInvalid(reason) を読んでもらうのはどうか。
    //   ERROR とかも同様に。
    //   renderJSON とか renderBinary とかも同様に出来る。
    //   xml で指定するんだったら自分でこういうの作ったほうが良い。
    //

	/** return code in case parameter is invalid. (ユーザーのリクエストがおかしい場合) */
	public static final String INVALID = "invalid"; //$NON-NLS-1$ //
	/** return code in case redirection */
	public static final String REDIRECT = "redirect"; //$NON-NLS-1$
	/** return code in case the required resource is not found.*/
	public static final String NOT_FOUND = "notfound"; //$NON-NLS-1$
	/** return code in case the operation is prohibited. */
	public static final String PROHIBITED = "prohibited"; //$NON-NLS-1$
	
	public static final String RETURNTOP = "returntop";
	
//	private static final String SUCCESS = "INTENTIONALLY SEAL THE ActionSupport.SUCCESS";
//	private static final String ERROR = "INTENTIONALLY SEAL THE ActionSupport.ERROR";
	
	// 様々なところで使うので、redirectURL を定義しておく。
	// あんまりよろしくないが、loginRequired でこれを使って、かつ login が必要なところは色色あるのでベースとして定義する
	protected String redirectURL;
	protected String currentURL;

    // These are used for returning stream.
    protected InputStream stream;
    protected String contentType;
    protected String contentDisposition;

	// somethingAware な人たち向け
    protected Map<String, Object> session = null;
    protected Map<String, Object> attributes = null;
    protected HttpServletRequest request = null;
    
    // ----------------------------------------------------------------------
    // 
    
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
        
        // session に、自分自身を付け加えておく。
        if (this.session != null) {
            this.session.put(Constants.ATTR_ACTION, this);
        }
    }
        
    @Override
    public void setRequest(Map<String, Object> attributes) {
    	this.attributes = attributes;
    }
    
    @Override
    public void setServletRequest(HttpServletRequest request) {
    	this.request = request;
    }
    
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

    public void addWarningMessage(String str) {
        addMessage(Constants.ATTR_WARNING_MESSAGE, str);
    }
    
    public Collection<String> getWarningMessages() {
        return getMessages(Constants.ATTR_WARNING_MESSAGE);
    }
    
    public void addErrorMessage(String str) {
        addMessage(Constants.ATTR_ERROR_MESSAGE, str);
    }
    
    public Collection<String> getErrorMessages() {
        return getMessages(Constants.ATTR_ERROR_MESSAGE);
    }
    
    private void addMessage(String key, String message) {
        @SuppressWarnings("unchecked")
        List<String> warningMessage = (List<String>) this.session.get(key);
        if (warningMessage == null) { warningMessage = new ArrayList<String>(); }

        warningMessage.add(message);
        this.session.put(key, warningMessage);
    }
    
    private Collection<String> getMessages(String key) {
        @SuppressWarnings("unchecked")
        List<String> warningMessage = (List<String>) this.session.get(key);
        if (warningMessage == null) { return new ArrayList<String>(); }
            
        this.session.put(key, null);
        return Collections.unmodifiableCollection(warningMessage);
    }

    // ----------------------------------------------------------------------
    // Utility function
    
    /**
     * get logged in user. If a user is not logged in, null is returned.
     * @return the logged in user. null if a user is not logged in.  
     */
    public UserEx getLoginUser() {
    	if (session == null) { return null; }
        return (UserEx) session.get(Constants.ATTR_USER);
    }    
    
    /**
     * ensure the user is logged in. If not logged in, LOGIN result exception will be raised.
     * @return the logged in user.
     * @throws PartakeResultException
     */
    public UserEx ensureLogin() throws PartakeResultException {
        UserEx user = getLoginUser();
        if (user == null) { throw new PartakeResultException(LOGIN); }
        return user;
    }
    
    // ----------------------------------------------------------------------
    // render functions
    
    /**
     * invalid user request.
     */
    protected String renderInvalid(String reason) {
        // TODO: reason should be added.
        return INVALID;
    }

    /**
     * occured an internal server error
     * @return
     */
    protected String redirectError(String reason) {
        // TODO: reason should be added.
        return ERROR;
    }
    
    /**
     * a utility function to show database error.
     * @return
     */
    protected String redirectDBError() {
        return redirectError(I18n.t(I18n.DATABASE_ERROR));
    }

    /**
     * show the 'not found' page.
     * @return
     */
    protected String renderNotFound() {
        return NOT_FOUND;
    }

    /**
     * show the 'forbidden' page when a user did something prohibited.
     * @return 
     */
    protected String renderForbidden() {
        return PROHIBITED;
    }
    
    /**
     * redirect to the specified URL.
     */
    protected String renderRedirect(String url) {
        this.redirectURL = url;
        return REDIRECT;
    }
    
    protected String renderLoginRequired() {
        return LOGIN;
    }
    
    protected String renderStream(InputStream stream, String contentType, String contentDisposition) {
        this.stream = stream;
        this.contentType = contentType;
        this.contentDisposition = contentDisposition;
        return "stream";
    }
    
    protected String renderAttachmentStream(InputStream stream, String contentType) {
        return renderStream(stream, contentType, "attachment");
    }
    
    protected String renderInlineStream(InputStream stream, String contentType) {
        return renderStream(stream, contentType, "inline");
    }
    
    protected String renderInlineStream(InputStream stream, String contentType, String filename) {
        String contentDisposition = String.format("inline; filename=\"%s\"", filename);
        return renderStream(stream, contentType, contentDisposition);
    }
    
    /** return contentType. This function is only valid when renderStream() has been called. */
    public String getContentType() {
        return this.contentType;
    }

    /** return input stream. This function is only valid when renderStream() has been called. */
    public InputStream getInputStream() {
        return stream;
    }

    public String getContentDisposition() {
        return this.contentDisposition;
    }
}
