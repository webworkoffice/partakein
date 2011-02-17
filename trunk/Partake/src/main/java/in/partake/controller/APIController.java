package in.partake.controller;

import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.I18n;
import in.partake.service.EventService;

import java.io.ByteArrayInputStream;

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
    
    // TODO: error のときに 400 bad request とか返したいねんけど...
    // struts だとこれ難しいかも。うーむ。struts 捨てたい。
    public String changeAttendance() throws PartakeResultException {
        UserEx user = ensureLogin();
        
        String userId = getParameter("userId");
        String eventId = getParameter("eventId");
        String status = getParameter("status");
        
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(eventId) || StringUtils.isEmpty(status)) {
            return ERROR;
        }
        
        try {
            EventEx event = EventService.get().getEventExById(eventId);
            if (event == null) { return ERROR; }
            if (!event.hasPermission(user, UserPermission.EVENT_EDIT_PARTICIPANTS)) {
                return ERROR;
            }
            
            if (EventService.get().updateAttendanceStatus(userId, eventId, AttendanceStatus.safeValueOf(status))) {
                inputStream = new ByteArrayInputStream("{\"status\": \"OK\"}".getBytes()); // TODO: とりあえず OK でお茶をにごすけど 200 なら OK を返したいのよ。
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            return ERROR;
        }
    }
}
