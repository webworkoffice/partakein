package in.partake.controller.action.auth;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade;
import in.partake.model.dto.User;
import in.partake.resource.Constants;
import in.partake.resource.UserErrorCode;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.ParameterList;

import com.opensymphony.xwork2.ActionContext;

public class VerifyForOpenIDAction extends AbstractOpenIDAction {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(VerifyForOpenIDAction.class);

    // なんでかしらないけど、login と connect の openID の URL を一緒にしないと残念なことになる。
    public String doExecute() throws DAOException {
        String purpose = (String) session.get(Constants.ATTR_OPENID_PURPOSE);
        session.remove(Constants.ATTR_OPENID_PURPOSE);

        if ("login".equals(purpose))
            return verifyOpenIDForLogin();
        if ("connect".equals(purpose))
            return verifyOpenIDForConnection();

        return renderInvalid(UserErrorCode.INVALID_OPENID_PURPOSE);
    }

    private String verifyOpenIDForLogin() throws DAOException {
        String identity = getIdentifier();
        if (identity == null) {
            logger.info("OpenID でのログインに失敗しました。");
            addWarningMessage("OpenID でのログインに失敗しました。");
            return renderRedirect("/");
        }

        // TODO: UserEx が identifier から取れるべき
        UserEx user = DeprecatedUserDAOFacade.get().loginByOpenID(identity);
        if (user != null) {
            session.put(Constants.ATTR_USER, user);
            return renderRedirect(getRedirectURL());
        } else {
            addWarningMessage("ログインに失敗しました。OpenID と twitter ID が結び付けられていません。 Twitter でログイン後、設定から Open ID との結び付けを行ってください。");
            return renderRedirect("/");
        }        
    }

    private String verifyOpenIDForConnection() throws DAOException {
        User user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        String identity = getIdentifier();
        if (identity == null)
            return renderInvalid(UserErrorCode.INVALID_OPENID_IDENTIFIER);

        DeprecatedUserDAOFacade.get().addOpenIDLinkage(user.getId(), identity);
        addActionMessage("OpenID との結びつけが成功しました");
        return renderRedirect("/mypage#account");
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

            VerificationResult verification = consumerManager.verify(receivingURL.toString(), response, discovered);

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
