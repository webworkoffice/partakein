package in.partake.controller;

import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.I18n;
import in.partake.service.EventService;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class APIController extends PartakeActionSupport {    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(APIController.class);
    
    private ByteArrayInputStream inputStream = null;
    
    public ByteArrayInputStream getInputStream() {
        return inputStream;
    }
    
    // ----------------------------------------------------------------------
    // event retrieval
    
    public String getEvent() {
        String eventId = getParameter("eventId");
        if (StringUtils.isEmpty(eventId)) { return INVALID; }
        
        UserEx loginUser = getLoginUser();
        
        try {
            EventEx event = EventService.get().getEventExById(eventId);
            if (StringUtils.isEmpty(eventId)) { return NOT_FOUND; }
            
            if (event.isPrivate()) {
                // TODO: EventsController とコードが同じなので共通化するべき　
                
                // owner および manager は見ることが出来る。
                String passcode = (String)session.get("event:" + eventId);
                if (loginUser != null && event.hasPermission(loginUser, UserPermission.EVENT_PRIVATE_EVENT)) {
                    // OK. You have the right to show this event.
                } else if (StringUtils.equals(event.getPasscode(), passcode)) {
                    // OK. The same passcode. 
                } else {
                    // public でなければ、passcode を入れなければ見ることが出来ない
                    return PROHIBITED;
                }
            }
            
            String json = event.toJSON();
            inputStream = new ByteArrayInputStream(json.getBytes("utf-8"));
            return SUCCESS;
            
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            return ERROR;
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException", e);
            return ERROR;
        }
    }
    
    
    
    // ----------------------------------------------------------------------
    // event
    
    public String changeAttendance() throws PartakeResultException {
        UserEx user = ensureLogin();
        
        String userId = getParameter("userId");
        String eventId = getParameter("eventId");
        String status = getParameter("status");
        
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(eventId) || StringUtils.isEmpty(status)) {
            return INVALID;
        }
        
        try {
            EventEx event = EventService.get().getEventExById(eventId);
            if (event == null) { return INVALID; }
            if (!event.hasPermission(user, UserPermission.EVENT_EDIT_PARTICIPANTS)) {
                return PROHIBITED;
            }
            
            if (EventService.get().updateAttendanceStatus(userId, eventId, AttendanceStatus.safeValueOf(status))) {
                inputStream = new ByteArrayInputStream("{\"status\": \"OK\"}".getBytes());
                return SUCCESS;
            } else {
                return INVALID;
            }
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            return ERROR;
        }
    }
}
