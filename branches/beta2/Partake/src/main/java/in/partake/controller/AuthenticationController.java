package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.User;
import in.partake.resource.Constants;
import in.partake.resource.PartakeProperties;
import in.partake.resource.ServerErrorCode;
import in.partake.service.UserService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ParameterList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.opensymphony.xwork2.ActionContext;

public class AuthenticationController extends PartakeActionSupport {
	/** */
	private static final long serialVersionUID = 1L;	
	private static final Logger logger = Logger.getLogger(AuthenticationController.class);

    private static ConsumerManager manager = null;
	
	static {
        try {
            manager = new ConsumerManager();
        } catch (ConsumerException e) {
            e.printStackTrace();
        }
	}
	
    public String loginRequired() {
        String url = getParameter(Constants.ATTR_REDIRECTURL);
        ActionContext.getContext().put(Constants.ATTR_REDIRECTURL, url);
        
        return SUCCESS;
    }

    public String logout() {
        if (session == null) { return ERROR; }
        
        // invalidate the session
        session.clear(); 
        
        addActionMessage("ログアウトしました。");
        return SUCCESS;
    }
    
    public String loginByTwitter() {
        if (session == null) { return ERROR; }
        
        Twitter twitter = new TwitterFactory().getInstance();
        
        session.put("twitter", twitter);
        try {
            // TODO: request.getContextPath() + "auth/twitter" とかやると twitter に飛ばされてしまう罠。
            // うーん、なんかきれいにならんもんかねえ。
            // servletpath とか設定すればいいんかな？
            
            String callbackURL = PartakeProperties.get().getTopPath() + "/auth/verifyForTwitter";
            RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL.toString());

            session.put("requestToken", requestToken);
            session.put("redirectURL", getParameter("redirectURL"));
            
            setRedirectURL(requestToken.getAuthenticationURL());
            return SUCCESS;
        } catch (TwitterException e) {
            return redirectError(ServerErrorCode.TWITTER_OAUTH_ERROR, e);
        }
    }
    
    public String verifyForTwitter() {
        if (session == null) { return ERROR; }
        
        Twitter twitter = (Twitter)session.get("twitter");
        RequestToken requestToken = (RequestToken)session.get("requestToken");
        
        String verifier = getParameter("oauth_verifier");
        String redirectURL = (String)session.get("redirectURL");

        try {
            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
            session.remove("requestToken");
            session.remove("redirectURL");

            UserEx user = UserService.get().loginUserByTwitter(twitter, accessToken);
            session.put(Constants.ATTR_USER, user);
        	
            addActionMessage("ログインしました");
            
        } catch (TwitterException e) {
        	e.printStackTrace();
        	return ERROR;
        } catch (DAOException e) {
            e.printStackTrace();
            return ERROR;
        }
        
        if (StringUtils.isEmpty(redirectURL)) {
        	return SUCCESS;
        } else {
            // If the redirect page is the error page, we do not want to show it. Showing the top page is better.
            String errorPageURL = PartakeProperties.get().getTopPath() + "/error";
            if (errorPageURL.equals(redirectURL)) {
                return SUCCESS;
            } else {
            	setRedirectURL(redirectURL);
            	return REDIRECT;
            }
        }
    }
        
    public String loginByOpenID() {
        String callbackURL = PartakeProperties.get().getTopPath() + "/auth/verifyOpenID";
        this.session.put(Constants.ATTR_OPENID_PURPOSE, "login");
        return loginByOpenID(callbackURL);
    }
    
    public String connectWithOpenID() {
        String callbackURL = PartakeProperties.get().getTopPath() + "/auth/verifyOpenID";
        this.session.put(Constants.ATTR_OPENID_PURPOSE, "connect");
        return loginByOpenID(callbackURL);        
    }
    
    // なんでかしらないけど、open id の URL を一緒にしないと残念なことになる。
    public String verifyOpenID() throws DAOException {
        String purpose = (String) session.get(Constants.ATTR_OPENID_PURPOSE);
        session.remove(Constants.ATTR_OPENID_PURPOSE);
        
        if ("login".equals(purpose)) {
            return verifyOpenIDForLogin();
        } else if ("connect".equals(purpose)) {
            return verifyOpenIDForConnection();
        } else {
            return INVALID;
        }
    }
    
    private String verifyOpenIDForLogin() throws DAOException {
        String identity = getIdentifier();
        if (identity == null) {
            logger.info("OpenID でのログインに失敗しました。");
            addWarningMessage("OpenID でのログインに失敗しました。");
            return RETURNTOP;
        }

        // TODO: UserEx が identifier から取れるべき
        UserEx user = UserService.get().loginByOpenID(identity);
        if (user != null) {
            session.put(Constants.ATTR_USER, user);
            return SUCCESS;
        } else {
            addWarningMessage("ログインに失敗しました。OpenID と twitter ID が結び付けられていません。 Twitter でログイン後、設定から Open ID との結び付けを行ってください。");
            return RETURNTOP;
        }        
    }
    
    private String verifyOpenIDForConnection() {
        User user = getLoginUser();
        if (user == null) {
            logger.info("sign in されていません");
            return ERROR; 
        }
        
        String identity = getIdentifier();
        if (identity == null) {
            logger.info("identity の取得に失敗しました");
            return ERROR;
        }
        
        try {
            UserService.get().addOpenIDLinkage(user.getId(), identity);
            addActionMessage("OpenID との結びつけが成功しました");
            
            this.redirectURL = PartakeProperties.get().getTopPath() + "/mypage#account";
            return REDIRECT;
        } catch (DAOException e) {
            logger.error("addOpenIDLinkage", e);
            return ERROR;
        }
    }
    
    
    // ----------------------------------------------------------------------
    
    private String loginByOpenID(String callbackURL) {
        String userSuppliedString = getParameter("openid_identifier");
        
        try {
            String returnToUrl = callbackURL.toString();
            List<?> discoveries = manager.discover(userSuppliedString);
            DiscoveryInformation discovered = manager.associate(discoveries);
            this.session.put(Constants.ATTR_OPENID_DISCOVERY_INFORMATION, discovered);
            AuthRequest authReq = manager.authenticate(discovered, returnToUrl);

            this.redirectURL = authReq.getDestinationUrl(true);
            return REDIRECT;
        } catch (OpenIDException e) {
            logger.info("OpenID login failed", e);
            
            // TODO: addActionError should be DEPRECATED.
            // We should use addErrorMessage.
            addActionError("OpenID でのログインに失敗しました。");
            
            this.redirectURL = PartakeProperties.get().getTopPath() + "/";
            return REDIRECT;
        }
    }
    
    private String getIdentifier() {
        try {
            // extract the parameters from the authentication response
            // (which comes in as a HTTP request from the OpenID provider)
            ParameterList response = new ParameterList(ActionContext.getContext().getParameters());

            // retrieve the previously stored discovery information
            DiscoveryInformation discovered = 
                (DiscoveryInformation) session.get(Constants.ATTR_OPENID_DISCOVERY_INFORMATION);

            // extract the receiving URL from the HTTP request
            // TODO: HttpServletRequest should be removed.
            HttpServletRequest httpReq = ServletActionContext.getRequest();
            StringBuffer receivingURL = httpReq.getRequestURL();
            String queryString = httpReq.getQueryString();
            if (queryString != null && queryString.length() > 0)
                receivingURL.append("?").append(httpReq.getQueryString());
            
            VerificationResult verification = manager.verify(receivingURL.toString(), response, discovered);
            
            // examine the verification result and extract the verified identifier
            Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                return verified.getIdentifier();
            }
            
        } catch (OpenIDException e) {
            logger.info("OpenIDException", e);            
        }
        
        return null;
    }
}
