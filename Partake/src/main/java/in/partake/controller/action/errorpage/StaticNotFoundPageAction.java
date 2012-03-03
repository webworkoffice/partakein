package in.partake.controller.action.errorpage;

import in.partake.controller.PartakeActionSupport;
import in.partake.model.dao.DAOException;

public class StaticNotFoundPageAction extends PartakeActionSupport {
    private static final long serialVersionUID = 1L;
    
    public String execute() throws DAOException {
        return render("notfound.jsp");
    }    
}
