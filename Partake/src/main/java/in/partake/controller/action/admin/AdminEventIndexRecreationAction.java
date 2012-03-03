package in.partake.controller.action.admin;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.resource.UserErrorCode;

public class AdminEventIndexRecreationAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    public String doExecute() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null || !user.isAdministrator())
            return renderForbidden();
        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SECURITY_CSRF);
        
        EventService.get().recreateEventIndex();
        
        addActionMessage("Event Index has been recreated.");
        return renderRedirect("/admin");
    }

}
