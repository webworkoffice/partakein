package in.partake.controller.action.admin;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;

public class AdminEventIndexRecreationAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    public String doExecute() throws DAOException, PartakeException {
        ensureAdmin(); 
        ensureValidSessionToken();
        
        DeprecatedEventDAOFacade.get().recreateEventIndex();
        
        addActionMessage("Event Index has been recreated.");
        return renderRedirect("/admin");
    }

}
