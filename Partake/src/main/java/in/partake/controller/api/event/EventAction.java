package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.UserErrorCode;
import in.partake.service.EventService;
import in.partake.service.MessageService;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public class EventAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;

    public String get() throws DAOException {
        String eventId = getParameter("eventId");
        if (StringUtils.isBlank(eventId))
            return renderInvalid(UserErrorCode.MISSING_EVENT_ID);        
        if (!Util.isUUID(eventId))
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);
        
        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null) { return renderInvalid(UserErrorCode.INVALID_EVENT_ID); } 
            
        if (event.isPrivate()) {
            // TODO: EventsController とコードが同じなので共通化するべき　
      
            // owner および manager は見ることが出来る。
            // TOOD: Use PartakeSession instead of session.
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
        obj.put("event", event.toSafeJSON());
        return renderOK(obj);
    }
    
    public String create() throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }

    public String modify() throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }
    
    public String remove() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();
        if (!checkSessionToken())
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

    public String enroll() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();
        if (!checkSessionToken())
            return renderInvalid(UserErrorCode.INVALID_SECURITY_CSRF);

        String eventId = getParameter("eventId");
        if (eventId == null) 
            return renderInvalid(UserErrorCode.MISSING_EVENT_ID);
        if (!Util.isUUID(eventId))
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        // If the comment does not exist, we use empty string instead.
        String comment = getParameter("comment");
        if (comment == null) { comment = ""; }
        if (comment.length() > 1024)
            return renderInvalid(UserErrorCode.INVALID_COMMENT_TOOLONG);

        ParticipationStatus status = ParticipationStatus.safeValueOf(getParameter("status"));
        if (status == null || status == ParticipationStatus.NOT_ENROLLED)
            return renderInvalid(UserErrorCode.INVALID_ENROLL_STATUS);
        
        throw new RuntimeException("Not implemented yet.");
//        try {
//            EventService.get().enrollForAPI(user, eventId, status, comment);
//            return renderOK();
//            
//        } catch (PartakeException e) {
//            return renderException(e);
//        }
    }

    public String sendMessage() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();
        if (!checkSessionToken())
            return renderInvalid(UserErrorCode.INVALID_SECURITY_CSRF);

        String eventId = getParameter("eventId");
        if (eventId == null)
            return renderInvalid(UserErrorCode.MISSING_EVENT_ID);
        if (!Util.isUUID(eventId))
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        String message = getParameter("message");
        if (StringUtils.isBlank(message))
            return renderInvalid(UserErrorCode.MISSING_MESSAGE);

        try {
            MessageService.get().sendMessage(user, eventId, message);
            return renderOK();
        } catch (PartakeException e) {
            return renderException(e);
        }
    }
    
    public String attend() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();
        if (!checkSessionToken())
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
