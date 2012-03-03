package in.partake.controller.api.event;

import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.UserErrorCode;

public class RemoveAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();
        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SECURITY_CSRF);
        
        String eventId = getParameter("eventId");
        if (eventId == null)
            return renderInvalid(UserErrorCode.MISSING_EVENT_ID);
        if (!Util.isUUID(eventId))
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null)
            return renderNotFound();
        
        if (!event.hasPermission(user, UserPermission.EVENT_REMOVE))
            return renderForbidden();

        EventService.get().remove(eventId);
        return renderOK();
    }
}
