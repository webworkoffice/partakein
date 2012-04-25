package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;

public class EventPermissionEditAction extends AbstractEventEditAction {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        String eventId = getValidEventIdParameter();

        event = new EventEditTransaction(eventId).execute();
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        if (!EventEditPermission.check(event, user))
            return renderForbidden(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        return render("events/edit_permission.jsp");
    }
}


