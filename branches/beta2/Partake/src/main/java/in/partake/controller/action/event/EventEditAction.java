package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.base.permission.UserPermission;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.resource.UserErrorCode;

public class EventEditAction extends AbstractEventEditAction {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        String eventId = getValidEventIdParameter();
        
        event = DeprecatedEventDAOFacade.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        if (!event.hasPermission(user, UserPermission.EVENT_EDIT))
            return renderForbidden(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        return render("events/edit.jsp");
    }
}