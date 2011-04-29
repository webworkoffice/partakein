package in.partake.controller.api.attendance;

import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.service.EventService;

public class AttendanceAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;

    public String change() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null) {
            return renderLoginRequired();
        }
        
        String userId = getParameter("userId");
        if (userId == null) { return renderInvalid("userId should be specified"); }
            
        String eventId = getParameter("eventId");
        if (eventId == null) { return renderInvalid("eventId should be specified"); }
        
        String status = getParameter("status");
        if (status == null) { return renderInvalid("status should be specified"); }
        
        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null) { return renderInvalid("invalid eventId was specified."); }
        
        if (!event.hasPermission(user, UserPermission.EVENT_EDIT_PARTICIPANTS)) {
            return renderForbidden();
        }
        
        if (EventService.get().updateAttendanceStatus(userId, eventId, AttendanceStatus.safeValueOf(status))) {
            return renderOK();
        } else {
            return renderInvalid("some invalid argument is specified.");
        }
    }

}
