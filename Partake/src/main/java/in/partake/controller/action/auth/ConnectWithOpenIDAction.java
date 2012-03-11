package in.partake.controller.action.auth;

import in.partake.base.PartakeException;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;

import org.openid4java.OpenIDException;

public class ConnectWithOpenIDAction extends AbstractOpenIDAction {
    private static final long serialVersionUID = 1L;

    public String doExecute() throws DAOException, PartakeException {
        try {
            ensureValidSessionToken();
            return doAuthenticate("connect");
        } catch (OpenIDException e) {
            return renderError(ServerErrorCode.OPENID_ERROR, e);
        }        
    }    
}
