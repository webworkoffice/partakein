package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.UserErrorCode;

public class EventEditAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    private EventEx event;
    
    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();

        String eventId = getValidEventIdParameter();
        event = EventService.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        if (!event.hasPermission(user, UserPermission.EVENT_EDIT))
            return renderForbidden(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        return render("events/edit.jsp");
    }

    public EventEx getEvent() {
        return event;
    }
}
