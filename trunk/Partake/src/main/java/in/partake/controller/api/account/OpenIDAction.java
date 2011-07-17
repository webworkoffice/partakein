package in.partake.controller.api.account;

import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.service.UserService;

import java.util.List;

import net.sf.json.JSONObject;

public class OpenIDAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;
    // private static final Logger logger = Logger.getLogger(OpenIDAction.class);

    public String removeOpenID() throws DAOException {
        // check login
        UserEx user = getLoginUser();
        if (user == null) { return renderLoginRequired(); }

        // check session token
        if (!checkSessionToken()) { return renderInvalid(UserErrorCode.INVALID_SESSION); } 

        // check arguments
        String identifier = getParameter("identifier");
        if (identifier == null) { return renderInvalid(UserErrorCode.MISSING_OPENID); }
        
        // execute
        
        // identifier が user と結び付けられているか検査して消去
        if (UserService.get().removeOpenIDLinkage(user.getId(), identifier)) {
            return renderOK();
        } else {
            return renderInvalid(UserErrorCode.INVALID_OPENID);
        }
    }

    public String getOpenID() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null) { return renderLoginRequired(); }

        // no need to check session token
        
        List<String> identifiers = UserService.get().getOpenIDIdentifiers(user.getId());
        if (identifiers == null) { return renderError(ServerErrorCode.LOGIC_ERROR); }

        JSONObject obj = new JSONObject();
        obj.put("identifiers", identifiers);

        return renderOK(obj);
    }
}
