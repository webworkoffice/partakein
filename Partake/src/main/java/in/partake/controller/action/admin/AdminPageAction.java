package in.partake.controller.action.admin;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;

public class AdminPageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    public String doExecute() throws DAOException, PartakeException {
        ensureAdmin(); 
        
        return render("admin/index.jsp");
    }
}
