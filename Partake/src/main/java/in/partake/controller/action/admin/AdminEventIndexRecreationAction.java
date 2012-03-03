package in.partake.controller.action.admin;

import in.partake.controller.DeprecatedPartakeActionSupport;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;
import in.partake.service.EventService;

public class AdminEventIndexRecreationAction extends DeprecatedPartakeActionSupport {
    private static final long serialVersionUID = 1L;

    public String execute() throws DAOException {
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
