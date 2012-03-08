package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.UserPermission;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.resource.UserErrorCode;

public class AttendAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String userId = getValidUserIdParameter();
        String eventId = getValidEventIdParameter();
        
        String status = getParameter("status");
        if (status == null || !AttendanceStatus.isValueOf(status))
            return renderInvalid(UserErrorCode.MISSING_ATTENDANCE_STATUS);
        
        // TODO: This should be transactional.
        EventEx event = DeprecatedEventDAOFacade.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);
        
        if (!event.hasPermission(user, UserPermission.EVENT_EDIT_PARTICIPANTS))
            return renderForbidden();
        
        if (DeprecatedEventDAOFacade.get().updateAttendanceStatus(userId, eventId, AttendanceStatus.safeValueOf(status)))
            return renderOK();
        else
            return renderInvalid(UserErrorCode.UNKNOWN_USER_ERROR);
    }
}
