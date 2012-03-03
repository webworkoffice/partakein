package in.partake.controller.action.auth;

import in.partake.model.dao.DAOException;
import in.partake.resource.Constants;
import in.partake.resource.PartakeProperties;

public class LoginByOpenIDAction extends AbstractOpenIDAction {
    private static final long serialVersionUID = 1L;
    
    public String doExecute() throws DAOException {
        String callbackURL = PartakeProperties.get().getTopPath() + "/auth/verifyOpenID";
        session.put(Constants.ATTR_OPENID_PURPOSE, "login");
        return loginByOpenID(callbackURL);
    }
}
