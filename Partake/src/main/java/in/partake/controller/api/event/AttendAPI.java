package in.partake.controller.api.event;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.UserErrorCode;

public class AttendAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();
        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SECURITY_CSRF);

        String userId = getParameter("userId");
        if (userId == null)
            return renderInvalid(UserErrorCode.MISSING_USER_ID);
            
        String eventId = getParameter("eventId");
        if (eventId == null)
            return renderInvalid(UserErrorCode.MISSING_EVENT_ID);
        
        String status = getParameter("status");
        if (status == null || !AttendanceStatus.isValueOf(status))
            return renderInvalid(UserErrorCode.MISSING_ATTENDANCE_STATUS);
        
        // TODO: This should be transactional.
        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);
        
        if (!event.hasPermission(user, UserPermission.EVENT_EDIT_PARTICIPANTS))
            return renderForbidden();
        
        if (EventService.get().updateAttendanceStatus(userId, eventId, AttendanceStatus.safeValueOf(status)))
            return renderOK();
        else
            return renderInvalid(UserErrorCode.UNKNOWN_USER_ERROR);
    }
}
