package in.partake.controller.action.errorpage;

import in.partake.controller.PartakeActionSupport;
import in.partake.model.dao.DAOException;

public class StaticInvalidPageAction extends PartakeActionSupport {
    private static final long serialVersionUID = 1L;
    
    public String execute() throws DAOException {
        return render("error/invalid.jsp");
    }    
}
