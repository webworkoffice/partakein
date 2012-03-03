package in.partake.controller.action.auth;

import in.partake.model.dao.DAOException;
import in.partake.resource.Constants;
import in.partake.resource.PartakeProperties;
import in.partake.resource.UserErrorCode;

public class ConnectWithOpenIDAction extends AbstractOpenIDAction {
    private static final long serialVersionUID = 1L;
    
    public String doExecute() throws DAOException {
        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SECURITY_CSRF);
        
        String callbackURL = PartakeProperties.get().getTopPath() + "/auth/verifyOpenID";
        this.session.put(Constants.ATTR_OPENID_PURPOSE, "connect");
        return loginByOpenID(callbackURL);        
    }

}
