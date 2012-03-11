package in.partake.controller.action.auth;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade;
import in.partake.model.dto.User;
import in.partake.resource.Constants;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.service.OpenIDService;
import in.partake.session.OpenIDLoginInformation;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.openid4java.OpenIDException;
import org.openid4java.discovery.DiscoveryInformation;

import com.opensymphony.xwork2.ActionContext;

public class VerifyForOpenIDAction extends AbstractOpenIDAction {
    private static final long serialVersionUID = 1L;

    // なんでかしらないけど、login と connect の openID の URL を一緒にしないと残念なことになる。
    public String doExecute() throws DAOException {
        OpenIDLoginInformation loginInformation = getPartakeSession().ensureOpenIDLoginInformation();
        String purpose = loginInformation.takeLoginPurpose();

        String receivingURL = getReceivingURL();
        Map<String, Object> params = ActionContext.getContext().getParameters();
        DiscoveryInformation discoveryInformation = loginInformation.getDiscoveryInformation();
        
        try {
            if ("login".equals(purpose))
                return verifyOpenIDForLogin(receivingURL, params, discoveryInformation);
            if ("connect".equals(purpose))
                return verifyOpenIDForConnection(receivingURL, params, discoveryInformation);
    
            return renderInvalid(UserErrorCode.INVALID_OPENID_PURPOSE);
        } catch (OpenIDException e) {
            return renderError(ServerErrorCode.OPENID_ERROR, e);
        }
    }

    private String verifyOpenIDForLogin(String receivingURL, Map<String, Object> params, DiscoveryInformation discoveryInformation) throws DAOException, OpenIDException {
        String identity = OpenIDService.getIdentifier(receivingURL, params, discoveryInformation);
        if (identity == null) {
            addWarningMessage("OpenID でのログインに失敗しました。");
            return renderRedirect("/");
        }

        // TODO: UserEx が identifier から取れるべき
        UserEx user = DeprecatedUserDAOFacade.get().loginByOpenID(identity);
        if (user != null) {
            session.put(Constants.ATTR_USER, user);
            if (getRedirectURL() == null)
                return renderRedirect("/");
            else
                return renderRedirect(getRedirectURL());
        } else {
            addWarningMessage("ログインに失敗しました。OpenID と twitter ID が結び付けられていません。 Twitter でログイン後、設定から Open ID との結び付けを行ってください。");
            return renderRedirect("/");
        }        
    }

    private String verifyOpenIDForConnection(String receivingURL, Map<String, Object> params, DiscoveryInformation discoveryInformation) throws DAOException, OpenIDException {
        User user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        String identity = OpenIDService.getIdentifier(receivingURL, params, discoveryInformation);
        if (identity == null)
            return renderInvalid(UserErrorCode.INVALID_OPENID_IDENTIFIER);

        DeprecatedUserDAOFacade.get().addOpenIDLinkage(user.getId(), identity);
        addActionMessage("OpenID との結びつけが成功しました");
        return renderRedirect("/mypage#account");
    }
    
    private String getReceivingURL() {
        // extract the receiving URL from the HTTP request
        // TODO: HttpServletRequest should be removed.
        HttpServletRequest httpReq = ServletActionContext.getRequest();
        StringBuffer receivingURL = httpReq.getRequestURL();
        String queryString = httpReq.getQueryString();
        if (queryString != null && queryString.length() > 0)
            receivingURL.append("?").append(httpReq.getQueryString());
        
        return receivingURL.toString();
    }
}
