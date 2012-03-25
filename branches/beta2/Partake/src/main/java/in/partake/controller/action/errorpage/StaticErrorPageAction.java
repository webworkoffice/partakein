package in.partake.controller.action.errorpage;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;

public class StaticErrorPageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    private ServerErrorCode errorCode;

    public String doExecute() throws DAOException {
        errorCode = ServerErrorCode.safeValueOf(getParameter("errorCode"));
        return render("error/error.jsp");
    }

    public ServerErrorCode getServerErrorCode() {
        return errorCode;
    }
}
