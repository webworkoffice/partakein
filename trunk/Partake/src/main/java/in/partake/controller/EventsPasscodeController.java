package in.partake.controller;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.service.EventService;

import org.apache.commons.lang.xwork.StringUtils;
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
		// validate() was already called, so it should be OK now.
		
		session.put("event:" + eventId, passcode);
		return SUCCESS;
	}
	
	@Override
	public void validate() {
		if (eventId == null) {
			addActionError("event が指定されていません。");
		}
		if (passcode == null) {
			addActionError("passcode が指定されていません");
		}

		try {
			Event event = EventService.get().getEventById(eventId);
			String pass = StringUtils.trim(passcode);
			
			if (!pass.equals(event.getPasscode())) {
				addFieldError("passcode", "passcode が一致しませんでした。");
			}
		} catch (DAOException e) {
		    logger.warn("validate() failed", e);
			addActionError("event を取得中にエラーが発生しました");
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
