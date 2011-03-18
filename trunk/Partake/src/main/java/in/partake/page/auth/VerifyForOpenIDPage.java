package in.partake.page.auth;

import in.partake.application.PartakeSession;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.page.base.PartakePage;
import in.partake.resource.Constants;
import in.partake.resource.I18n;
import in.partake.resource.PartakeProperties;
import in.partake.service.UserService;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.ParameterList;

import com.opensymphony.xwork2.ActionContext;

public class VerifyForOpenIDPage extends PartakePage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(VerifyForOpenIDPage.class);
    

    public VerifyForOpenIDPage(PageParameters params) {
        PartakeSession session = PartakeSession.get();
        
        String purpose = (String) session.get(Constants.ATTR_OPENID_PURPOSE);
        session.remove(Constants.ATTR_OPENID_PURPOSE);
        
        if ("login".equals(purpose)) {
            verifyOpenIDForLogin();
        } else if ("connect".equals(purpose)) {
            verifyOpenIDForConnection();
        } else {
            renderInvalidRequest("OpenID ログインシーケンスが不正です。");
        }
    }
    
    private void verifyOpenIDForLogin() {
        PartakeSession session = PartakeSession.get();
        
        String identity = getIdentifier();
        if (identity == null) {
            session.addWarningMessage("OpenID でのログインに失敗しました。");
            renderRedirect("/");
            return;
        }
        
        try {
            // TODO: UserEx が identifier から取れるべき
            UserEx user = UserService.get().loginByOpenID(identity);
            if (user != null) {
                session.setCurrentUser(user);
                renderRedirect("/"); // TODO: redirect 先が指定できるべき 
            } else {
                session.addWarningMessage("ログインに失敗しました。OpenID と twitter ID が結び付けられていません。 Twitter でログイン後、設定から Open ID との結び付けを行ってください。");
                renderRedirect("/"); // TODO: redirect 先が指定できるべき 
            }
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
        }
    }
    
    private void verifyOpenIDForConnection() {
        PartakeSession session = PartakeSession.get();
        UserEx user = session.getCurrentUser();
        if (user == null) {
            renderInvalidRequest("OpenID との結びつけが選択されましたが、ログインされていません。");
            return;
        }
        
        String identity = getIdentifier();
        if (identity == null) {
            renderError("identity の取得に失敗しました");
            return;
        }
        
        try {
            UserService.get().addOpenIDLinkage(user.getId(), identity);
            session.addMessage("OpenID との結びつけが成功しました");
            
            renderRedirect(PartakeProperties.get().getTopPath() + "/preference");
            return;
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
        }
    }
    
    private String getIdentifier() {
        try {
            PartakeSession session = PartakeSession.get();
            
            // extract the parameters from the authentication response
            // (which comes in as a HTTP request from the OpenID provider)
            ParameterList response = new ParameterList(ActionContext.getContext().getParameters());

            // retrieve the previously stored discovery information
            DiscoveryInformation discovered = 
                (DiscoveryInformation) session.get(Constants.ATTR_OPENID_DISCOVERY_INFORMATION);

            // extract the receiving URL from the HTTP request
            ServletWebRequest servletWebRequest = (ServletWebRequest) getRequest();
            HttpServletRequest httpReq = servletWebRequest.getContainerRequest();
            
            StringBuffer receivingURL = httpReq.getRequestURL();
            String queryString = httpReq.getQueryString();
            if (queryString != null && queryString.length() > 0) {
                receivingURL.append("?").append(httpReq.getQueryString());
            }
            
            VerificationResult verification = session.getConsumerManager().verify(receivingURL.toString(), response, discovered);
            
            // examine the verification result and extract the verified identifier
            Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                return verified.getIdentifier();
            }
            
            return null;
        } catch (OpenIDException e) {
            logger.info("OpenIDException", e);       
            return null;
        }
    }
}
