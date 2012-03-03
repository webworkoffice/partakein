package in.partake.controller.action.admin;

import in.partake.controller.PartakeActionSupport;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.service.EventService;

public class AdminEventIndexRecreationAction extends PartakeActionSupport {
    private static final long serialVersionUID = 1L;

    public String execute() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null || !user.isAdministrator())
            return renderForbidden();
        
        EventService.get().recreateEventIndex();
        
        addActionMessage("Event Index has been recreated.");
        return renderRedirect("/admin");
    }

}
