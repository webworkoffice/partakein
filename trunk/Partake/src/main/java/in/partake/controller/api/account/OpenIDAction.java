package in.partake.controller.api.account;

import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;
import in.partake.service.UserService;

public class OpenIDAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;    
    // private static final Logger logger = Logger.getLogger(OpenIDAction.class);
    
    public String removeOpenID() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null) { return renderLoginRequired(); }

        String identifier = getParameter("identifier");
        if (identifier == null) {
            return renderInvalid(UserErrorCode.MISSING_OPENID);
        }
        
        // identifier が user と結び付けられているか検査して消去
        if (UserService.get().removeOpenIDLinkage(user.getId(), identifier)) {
            return renderOK();
        } else {
            return renderInvalid(UserErrorCode.INVALID_OPENID);
        }
    }
}
