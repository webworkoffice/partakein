package in.partake.controller.action.auth;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.resource.MessageCode;

public class LogoutAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    public String doExecute() throws DAOException {
        if (session != null)
            session.clear();

        return renderRedirect("/", MessageCode.MESSAGE_AUTH_LOGOUT);
    }
}
