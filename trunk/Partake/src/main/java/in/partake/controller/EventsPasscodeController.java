package in.partake.controller;

import in.partake.model.dto.Event;
import in.partake.service.EventService;

import org.apache.commons.lang.xwork.StringUtils;

import com.opensymphony.xwork2.Validateable;


public class EventsPasscodeController extends PartakeActionSupport implements Validateable {
	private static final long serialVersionUID = 1L;
	
	private String eventId;
	private String passcode;
	
	// /events/passcode
	public String passcode() {
		// validate() was already called, so it should be OK now.
		
		session.put("event:" + eventId, passcode);
		System.out.println("SEEMS OK");
		return SUCCESS;
	}
	
	@Override
	public void validate() {
		System.out.println("PasscodeController validate() called.");
		
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
		} catch (Exception e) {
			addActionError("event を取得中にエラーが発生しました");
		}
	}
	
	public void setEventId(String eventId) {
		System.out.println("PasscodeController setEventId() called.");
		this.eventId = eventId;
	}
	
	public String getEventId() {
		return this.eventId;
	}

	public void setPasscode(String passcode) {
		System.out.println("PasscodeController passcode() called.");
		this.passcode = passcode;
	}
}
