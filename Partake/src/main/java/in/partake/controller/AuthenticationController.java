package in.partake.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.resource.Constants;
import in.partake.resource.PartakeProperties;
import in.partake.service.UserService;
import in.partake.util.Util;

import com.opensymphony.xwork2.ActionContext;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class AuthenticationController extends PartakeActionSupport {
    private static final Logger logger = Logger.getLogger(AuthenticationController.class);
    
	private static final long serialVersionUID = 1L;
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
            e.printStackTrace();
            return ERROR;
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

            twitter4j.User twitterUser = twitter.showUser(twitter.getId()); 
            TwitterLinkage twitterLinkageEmbryo = new TwitterLinkage(
        			twitter.getId(), twitter.getScreenName(), twitterUser.getName(), accessToken.getToken(), accessToken.getTokenSecret(),
        			twitter.showUser(twitter.getId()).getProfileImageURL().toString(), null
        	);

            // Twitter Linkage から User を引いてくる。
            // 対応する user がいない場合は、user を作成して Twitter Linkage を付与する
            
            // 1. まず TwitterLinkage を作成 / アップデート            
            TwitterLinkage twitterLinkage = UserService.get().updateTwitterLinkage(twitterLinkageEmbryo, twitter); 

            // 2. 対応するユーザーを生成　TODO: UserSerivce で一気にやってしまうべき
        	User user = UserService.get().getUserFromTwitterLinkage(twitterLinkage, twitter, true);
            UserEx userEx = UserService.get().getPartakeUserByUser(user);
        	
            // 3. lastlogin の update
        	UserService.get().updateLastLogin(user);
        	
            session.put(Constants.ATTR_USER, userEx);
            addActionMessage("ログインしました");
            
        } catch (TwitterException e) {
        	e.printStackTrace();
        	return ERROR;
        } catch (DAOException e) {
            e.printStackTrace();
            return ERROR;
        }
        
        if (Util.isEmpty(redirectURL)) {
        	return SUCCESS;
        } else {
        	setRedirectURL(redirectURL);
        	return REDIRECT;        	
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
    public String verifyOpenID() {
        String purpose = (String) session.get(Constants.ATTR_OPENID_PURPOSE);
        session.remove(Constants.ATTR_OPENID_PURPOSE);
        
        if ("login".equals(purpose)) {
            return verifyOpenIDForLogin();
        } else if ("connect".equals(purpose)) {
            return verifyOpenIDForConnection();
        } else {
            return ERROR;
        }
    }
    
    private String verifyOpenIDForLogin() {
        String identity = getIdentifier();
        if (identity == null) {
            logger.info("OpenID でのログインに失敗しました。");
            addWarningMessage("OpenID でのログインに失敗しました。");
            return RETURNTOP;
        }
        
        try {
            User user = UserService.get().getUserFromOpenIDLinkage(identity);
            if (user != null) {
                UserEx userEx = UserService.get().getPartakeUserByUser(user);
                session.put(Constants.ATTR_USER, userEx);
                return SUCCESS;
            } else {
                addWarningMessage("ログインに失敗しました。OpenID と twitter ID が結び付けられていません。 Twitter でログイン後、設定から Open ID との結び付けを行ってください。");
                return RETURNTOP;
            }
        } catch (DAOException e) {
            logger.error("DAOException", e);
            addActionError("データベースエラーが発生しました。"); // TODO: なんか ActionError の使い方が間違っているきがするんだよなあ...。
            return ERROR;
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
            
            this.redirectURL = PartakeProperties.get().getTopPath() + "/preference";
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
            List discoveries = manager.discover(userSuppliedString);
            DiscoveryInformation discovered = manager.associate(discoveries);
            this.session.put(Constants.ATTR_OPENID_DISCOVERY_INFORMATION, discovered);
            AuthRequest authReq = manager.authenticate(discovered, returnToUrl);

            this.redirectURL = authReq.getDestinationUrl(true);
            return REDIRECT;
        } catch (OpenIDException e) {
            logger.info("OpenID login failed", e);
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
