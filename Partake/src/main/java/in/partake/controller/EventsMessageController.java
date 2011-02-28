package in.partake.controller;

import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dto.Message;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.service.EventService;
import in.partake.service.MessageService;
import in.partake.util.Util;

import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.ValidatorType;


public class EventsMessageController extends PartakeActionSupport {
	/** */
	private static final long serialVersionUID = 1L;
	// private static final Logger logger = Logger.getLogger(EventsMessageController.class);
	
	private String eventId;
	private String message;
	
	public String send() {
		if (eventId == null) { return ERROR; }
		if (message == null) { return ERROR; }

		UserEx user = getLoginUser();
		if (user == null) { return ERROR; }

		try {
			EventEx event = EventService.get().getEventExById(eventId);
			if (event == null) { return ERROR; }

			if (!event.hasPermission(user, UserPermission.EVENT_SEND_MESSAGE)) { return PROHIBITED; }
			
			// ５つメッセージを取ってきて、制約をみたしているかどうかチェックする。
			List<Message> messages = MessageService.get().getRecentUserMessage(eventId, 5);
			Date currentTime = new Date(); 

			if (messages.size() >= 3) {
				Message msg = messages.get(2);
				Date msgDate = msg.getCreatedAt();
				Date thresholdDate = new Date(msgDate.getTime() + 1000 * 60 * 60); // one hour later after the message was sent.
				if (currentTime.before(thresholdDate)) { // NG
					addWarningMessage("メッセージは１時間に３通、１日に５通までしか送ることが出来ません。"); // TODO: addActionError だとおかしい。addErrorMessage() をつくるべきか？
                    return INPUT;
				}
			}
			if (messages.size() >= 5) {
				Message msg = messages.get(4);
				Date msgDate = msg.getCreatedAt();
				Date thresholdDate = new Date(msgDate.getTime() + 1000 * 60 * 60 * 24); // one day later after the message was sent.
				
				if (currentTime.before(thresholdDate)) { // NG
					addWarningMessage("メッセージは１時間に３通、１日に５通までしか送ることが出来ません。");
                    return INPUT;
				}
			}

			assert (message != null && message.length() <= 100);
			String left = "[PARTAKE] 「";
			String right = String.format("」 %s の管理者(@%s)よりメッセージ：%s", event.getShortenedURL(), user.getScreenName(), message);
			if (Util.codePointCount(left) + Util.codePointCount(right) > 140) {
				addWarningMessage("メッセージの長さをもう少し短くしてください。");
				return INPUT;
			}
			
			String msg = left + Util.shorten(event.getTitle(), 140 - Util.codePointCount(left) - Util.codePointCount(right)) + right;
			assert (Util.codePointCount(msg) <= 140);
			
			String messageId = MessageService.get().addMessage(user.getId(), msg,event.getId(), true);
			
			List<Enrollment> participations = EventService.get().getParticipation(event.getId());
			for (Enrollment participation : participations) {
			    boolean sendsMessage = false;
			    switch (participation.getStatus()) {
			    case ENROLLED:
			        sendsMessage = true; break;
			    case RESERVED:
			        sendsMessage = true; break;
			    default:
			        break;
			    }
			    
			    if (sendsMessage) {
			        MessageService.get().sendEnvelope(messageId, participation.getUserId(), participation.getUserId(), null, DirectMessagePostingType.POSTING_TWITTER_DIRECT);
			    }
			}
			
			addActionMessage("メッセージを送信しました");			
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
	}
	
	// ----------------------------------------------------------------------
	
	public void validate() {
	}
	
	// ----------------------------------------------------------------------
	
	public String getEventId() {
		return this.eventId;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	// ----------------------------------------------------------------------
	
	@RequiredFieldValidator(type = ValidatorType.FIELD, message = "ID が不正です")
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "メッセージが必要です")
    @StringLengthFieldValidator(type = ValidatorType.FIELD, maxLength = "100", message = "message は 100 文字以下で記述してください")
    @RequiredStringValidator(type = ValidatorType.FIELD, message = "メッセージが必要です")
	public void setMessage(String message) {
		this.message = message;
	}
	
}
