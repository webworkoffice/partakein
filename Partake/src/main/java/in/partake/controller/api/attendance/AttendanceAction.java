package in.partake.controller.api.attendance;

import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.UserErrorCode;
import in.partake.service.EventService;

public class AttendanceAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;

    /**
     * 出欠を変更します。
     * @return
     * @throws DAOException
     */
    public String change() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null) {
            return renderLoginRequired();
        }

        assert getPartakeSession() != null;
        assert getPartakeSession().getCSRFPrevention() != null;

        String userId = getParameter("userId");
        if (userId == null) { return renderInvalid(UserErrorCode.MISSING_USER_ID); }
            
        String eventId = getParameter("eventId");
        if (eventId == null) { return renderInvalid(UserErrorCode.MISSING_EVENT_ID); }
        
        String status = getParameter("status");
        if (status == null || !AttendanceStatus.isValueOf(status)) {
            return renderInvalid(UserErrorCode.MISSING_ATTENDANCE_STATUS);
        }
        
        // To prevent CSRF, we should check token.
        String token = getParameter("sessionToken");
        if (!getPartakeSession().getCSRFPrevention().isValidSessionToken(token)) {
            return renderInvalid(UserErrorCode.INVALID_SESSION);
        }
        
        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null) { return renderInvalid(UserErrorCode.INVALID_EVENT_ID); }
        
        if (!event.hasPermission(user, UserPermission.EVENT_EDIT_PARTICIPANTS)) {
            return renderForbidden();
        }
        
        if (EventService.get().updateAttendanceStatus(userId, eventId, AttendanceStatus.safeValueOf(status))) {
            return renderOK();
        } else {
            return renderInvalid(UserErrorCode.UNKNOWN_USER_ERROR);
        }
    }

}
