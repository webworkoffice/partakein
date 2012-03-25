package in.partake.controller.action.auth;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.OpenIDLinkage;
import in.partake.model.dto.User;
import in.partake.resource.Constants;
import in.partake.resource.MessageCode;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
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
    public String doExecute() throws DAOException, PartakeException {
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

    private String verifyOpenIDForLogin(String receivingURL, Map<String, Object> params, DiscoveryInformation discoveryInformation) throws DAOException, OpenIDException, PartakeException {
        String identity = PartakeApp.getOpenIDService().getIdentifier(receivingURL, params, discoveryInformation);
        if (identity == null) {
            return renderRedirect("/", MessageCode.MESSAGE_OPENID_LOGIN_FAILURE);
        }

        // TODO: UserEx が identifier から取れるべき
        UserEx user = new GetUserFromOpenIDIdentifierTransaction(identity).execute();
        if (user != null) {
            session.put(Constants.ATTR_USER, user);
            if (getRedirectURL() == null)
                return renderRedirect("/");
            else
                return renderRedirect(getRedirectURL());
        } else {
            return renderRedirect("/", MessageCode.MESSAGE_OPENID_LOGIN_NOLINKAGE);
        }
    }

    private String verifyOpenIDForConnection(String receivingURL, Map<String, Object> params, DiscoveryInformation discoveryInformation) throws DAOException, PartakeException, OpenIDException {
        User user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        String identity = PartakeApp.getOpenIDService().getIdentifier(receivingURL, params, discoveryInformation);
        if (identity == null)
            return renderInvalid(UserErrorCode.INVALID_OPENID_IDENTIFIER);

        new AddOpenIDTransaction(user.getId(), identity).execute();

        return renderRedirect("/mypage#account", MessageCode.MESSAGE_OPENID_CONNECTION_SUCCESS);
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

class GetUserFromOpenIDIdentifierTransaction extends DBAccess<UserEx> {
    private String identifier;

    GetUserFromOpenIDIdentifierTransaction(String identifier) {
        this.identifier = identifier;
    }

    @Override
    protected UserEx doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        OpenIDLinkage linkage = daos.getOpenIDLinkageAccess().find(con, identifier);
        if (linkage == null)
            return null;

        return UserDAOFacade.getUserEx(con, daos, linkage.getUserId());
    }
}

class AddOpenIDTransaction extends Transaction<Void> {
    private String userId;
    private String identifier;

    public AddOpenIDTransaction(String userId, String identifier) {
        this.userId = userId;
        this.identifier = identifier;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        daos.getOpenIDLinkageAccess().put(con, new OpenIDLinkage(identifier, userId));
        return null;
    }
}
