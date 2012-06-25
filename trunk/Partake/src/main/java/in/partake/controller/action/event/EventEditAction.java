package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.EventEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.resource.UserErrorCode;

public class EventEditAction extends AbstractEventEditAction {
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

        return render("events/edit_basic.jsp");
    }
}

class EventEditTransaction extends DBAccess<EventEx> {
    private String eventId;

    public EventEditTransaction(String eventId) {
        this.eventId = eventId;
    }

    @Override
    protected EventEx doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        return EventDAOFacade.getEventEx(con, daos, eventId);
    }
}
