package in.partake.controller.action.errorpage;

import in.partake.controller.DeprecatedPartakeActionSupport;
import in.partake.model.dao.DAOException;

public class StaticForbiddenPageAction extends DeprecatedPartakeActionSupport {
    private static final long serialVersionUID = 1L;
    
    public String execute() throws DAOException {
        return render("error/prohibited.jsp");
    }    
}
