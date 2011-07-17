package in.partake.controller.api.event;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.UserErrorCode;
import in.partake.service.EventService;

public class EventAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;

    public String get() throws DAOException {
        String eventId = getParameter("eventId");
        if (StringUtils.isBlank(eventId)) { return renderInvalid(UserErrorCode.MISSING_EVENT_ID); } 
        
        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null) { return renderInvalid(UserErrorCode.INVALID_EVENT_ID); } 
            
        if (event.isPrivate()) {
            // TODO: EventsController とコードが同じなので共通化するべき　
      
            // owner および manager は見ることが出来る。
            String passcode = (String) session.get("event:" + eventId);
            if (passcode == null) { passcode = getParameter("passcode"); }
      
            UserEx loginUser = getLoginUser();
            if (loginUser != null && event.hasPermission(loginUser, UserPermission.EVENT_PRIVATE_EVENT)) {
                // OK. You have the right to show this event.
            } else if (StringUtils.equals(event.getPasscode(), passcode)) {
                // OK. The same passcode. 
            } else {
                // public でなければ、passcode を入れなければ見ることが出来ない
                return renderForbidden();
            }
        }

        JSONObject obj = new JSONObject();
        obj.put("event", event.toJSON());
        return renderOK(obj);
    }
    
    public String create() throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }

    public String modify() throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }

    public String search() throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }

    public String enroll() throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }

    public String comments() throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }

    public String messages() throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }

    public String sendMessage() throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }
    
    public String attendance() throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }

    public String attend() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null) { return renderLoginRequired(); }

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
