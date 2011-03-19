package in.partake.controller;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.resource.I18n;
import in.partake.service.EventService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.opensymphony.xwork2.Validateable;


public class EventsPasscodeController extends PartakeActionSupport implements Validateable {
	/** */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(EventsPasscodeController.class);
	
	private String eventId;
	private String passcode;
	
	// /events/passcode
	public String passcode() {
	    try {
	        if (passcode == null) {
	            return INPUT;
	        }
	        
	        Event event = EventService.get().getEventById(eventId);
	        if (event == null) {
	            logger.info("無効な eventId です。");
	            return ERROR;
	        }
	        
            String pass = StringUtils.trim(passcode);
            
            if (!pass.equals(event.getPasscode())) {
                addWarningMessage("passcode が一致しませんでした。");
                return INPUT;
            }
            
            session.put("event:" + eventId, passcode);
            return SUCCESS;
	    } catch (DAOException e) {
            logger.warn(I18n.t(I18n.DATABASE_ERROR), e);
            addActionError(I18n.t(I18n.DATABASE_ERROR));
	    }
	    
		session.put("event:" + eventId, passcode);
		return SUCCESS;
	}
	
	@Override
	public void validate() {
		if (StringUtils.isEmpty(eventId)) {
			addActionError("event が指定されていません。");			
		}
	}
	
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
	public String getEventId() {
		return this.eventId;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}
}
