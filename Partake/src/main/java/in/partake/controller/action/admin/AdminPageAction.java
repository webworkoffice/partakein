package in.partake.controller.action.admin;

import in.partake.controller.PartakeActionSupport;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;

public class AdminPageAction extends PartakeActionSupport {
    private static final long serialVersionUID = 1L;

    public String execute() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null || !user.isAdministrator())
            return renderForbidden();
        
        return render("admin/index.jsp");
    }
}
