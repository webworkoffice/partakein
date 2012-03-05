package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.UserErrorCode;

import org.apache.commons.lang.StringUtils;

public class RemoveAttendantAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();

        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SECURITY_CSRF);
        
        String eventId = getParameter("eventId");
        if (StringUtils.isEmpty(eventId))
            return renderInvalid(UserErrorCode.MISSING_EVENT_ID);
        if (!Util.isUUID(eventId))
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        String userId = getValidUserIdParameter();
            
        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        // Only owner can retrieve the participants list.
        if (!event.hasPermission(user, UserPermission.EVENT_EDIT_PARTICIPANTS))
            return renderInvalid(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);
            
        if (EventService.get().removeEnrollment(eventId, userId))
            return renderOK();
        
        return renderInvalid(UserErrorCode.INVALID_ATTENDANT_EDIT);
    }
}
