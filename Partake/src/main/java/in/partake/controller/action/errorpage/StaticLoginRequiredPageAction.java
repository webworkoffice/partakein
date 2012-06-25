package in.partake.controller.action.errorpage;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;

public class StaticLoginRequiredPageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    
    public String doExecute() throws DAOException {
        // If a user already has logged in, redirect to the top page.
        UserEx user = getLoginUser();
        if (user != null)
            return renderRedirect("/");
        
        return render("error/loginRequired.jsp");
    }    
}
