package in.partake.controller.action.errorpage;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;

public class StaticInvalidPageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    
    public String doExecute() throws DAOException {
        return render("error/invalid.jsp");
    }    
}
