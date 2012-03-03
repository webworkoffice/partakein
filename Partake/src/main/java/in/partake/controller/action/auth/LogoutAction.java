package in.partake.controller.action.auth;

import in.partake.controller.DeprecatedPartakeActionSupport;
import in.partake.model.dao.DAOException;

public class LogoutAction extends DeprecatedPartakeActionSupport {
    private static final long serialVersionUID = 1L;
    // private static final Logger logger = Logger.getLogger(LogoutAction.class);

    public String execute() throws DAOException {
        if (session != null)
            session.clear();
        
        addActionMessage("ログアウトしました。");
        return renderRedirect("/");
    }
}
