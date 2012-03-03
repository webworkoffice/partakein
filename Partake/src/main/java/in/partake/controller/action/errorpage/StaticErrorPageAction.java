package in.partake.controller.action.errorpage;

import in.partake.controller.DeprecatedPartakeActionSupport;
import in.partake.model.dao.DAOException;

public class StaticErrorPageAction extends DeprecatedPartakeActionSupport {
    private static final long serialVersionUID = 1L;
    
    public String execute() throws DAOException {
        return render("error/error.jsp");
    }    
}
