package in.partake.controller.action.errorpage;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;

public class StaticInvalidPageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    private UserErrorCode errorCode;

    public String doExecute() throws DAOException {
        errorCode = UserErrorCode.safeValueOf(getParameter("errorCode"));
        return render("error/invalid.jsp");
    }

    public UserErrorCode getUserErrorCode() {
        return errorCode;
    }
}
