package in.partake.controller;

import in.partake.model.CommentEx;
import in.partake.model.EventEx;
import in.partake.model.ParticipationEx;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dto.Comment;
import in.partake.model.dto.Event;
import in.partake.model.dto.ParticipationStatus;
import in.partake.model.dto.UserPermission;
import in.partake.model.dto.UserPreference;
import in.partake.resource.Constants;
import in.partake.service.DirectMessageService;
import in.partake.service.EventService;
import in.partake.service.MessageService;
import in.partake.service.UserService;
import in.partake.util.Util;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.xwork.StringUtils;
import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;

import com.opensymphony.xwork2.Validateable;

public class EventsController extends PartakeActionSupport implements Validateable {
    private static final Logger logger = Logger.getLogger(EventsController.class);
    
	private static final long serialVersionUID = 1L;

    private String eventId;
    private EventEx event;
        
    public String getEventId() {
    	return eventId;
    }
    
    public EventEx getEvent() {
        return event;
    }

    // GET /events/:id
    public String show() {
    	String eventId = getParameter("eventId");        
        if (eventId == null) { return NOT_FOUND; }
        
     	// NOTE: login はしてないかもしれない。
        UserEx user = getLoginUser();
        
	    try {
	        EventEx event = EventService.get().getEventExById(eventId);
	        if (event == null) { return NOT_FOUND; }
	        	        
	        if (event.isPrivate()) {
	            // owner および manager は見ることが出来る。
	        	String passcode = (String)session.get("event:" + eventId);
	        	if (user != null && event.hasPermission(user, UserPermission.EVENT_PRIVATE_EVENT)) {
	        		// OK. You have the right to show this event.
	        	} else if (StringUtils.equals(event.getPasscode(), passcode)) {
	        		// OK. The same passcode. 
	        	} else {
	        		// public でなければ、passcode を入れなければ見ることが出来ない
		        	this.eventId = eventId;
		        	return INPUT; // passcode required.	 
	        	}
	        }
	        
	        List<ParticipationEx> participations = EventService.get().getParticipationEx(event.getId());
	        if (participations == null) { 
	            logger.error("Getting participation failed.");
	        	return ERROR;
	        }
	        
	        ParticipationList participationList = event.calculateParticipationList(participations);
	        	        
	        Date deadline = event.getDeadline();
	        if (deadline == null) {
	        	deadline = event.getBeginDate();
	        }
	        boolean deadlineOver = deadline.before(new Date());
	        
	        
	        DataIterator<CommentEx> comments = EventService.get().getCommentsExByEvent(eventId); 
	        
	        attributes.put(Constants.ATTR_EVENT, event);
	        attributes.put(Constants.ATTR_ENROLLED_PARTICIPATIONS, participationList.getEnrolledParticipations());
	        attributes.put(Constants.ATTR_SPARE_PARTICIPATIONS, participationList.getSpareParticipations());
	        attributes.put(Constants.ATTR_CANCELLED_PARTICIPATIONS, participationList.getCancelledParticipations());
	        
	        if (user != null) {
	        	attributes.put(Constants.ATTR_PARTICIPATION_STATUS, UserService.get().getParticipationStatus(user, event));
	        } else {
	        	attributes.put(Constants.ATTR_PARTICIPATION_STATUS, ParticipationStatus.NOT_ENROLLED);
	        }
	        attributes.put(Constants.ATTR_DEADLINE_OVER, Boolean.valueOf(deadlineOver));
	        attributes.put(Constants.ATTR_COMMENTSET, comments);
	        attributes.put(Constants.ATTR_NOTIFICATION_STATUS, MessageService.get().getNotificationStatus(eventId));
	        
	        this.eventId = eventId;
	        this.event = event;
	        
	        return SUCCESS;
	    } catch (DAOException e) {
	    	e.printStackTrace();
	    	return ERROR;
	    }
    }
    
    // comment を post
    public String comment() throws PartakeResultException {
        // In order to comment, a user should be logged in.
        UserEx user = ensureLogin();
        
        if (user == null) { return LOGIN; }

        this.eventId = getParameter("eventId");
   		if (eventId == null) {
   			logger.info("EVENT ID was not set correctly."); // TODO: あー国際化したい
   			return ERROR;
   		}
   		
   		try {
            event = EventService.get().getEventExById(eventId);
            if (event == null) {
                logger.warn("EVENT ID : "  + eventId + " was not found.");
                return NOT_FOUND;
            }
	   		
	   		String comment = getParameter("comment");
	   		if (StringUtils.isEmpty(comment)) {
	   		    addFieldError("comment", "コメントを入力してください。");
	   		    return INPUT;
	   		}
	   		
	   		Comment embryo = new Comment(eventId, user.getId(), comment);
	   		EventService.get().addComment(embryo);
	   		
	   		return SUCCESS;
    	} catch (DAOException e){
    		e.printStackTrace();
    		return ERROR;
    	}
    }

    public String enroll() throws PartakeResultException {
    	return changeParticipationStatus(ParticipationStatus.ENROLLED, false, false);
    }
    
    public String reserve() throws PartakeResultException {
    	return changeParticipationStatus(ParticipationStatus.RESERVED, false, false);
    }
    
    public String cancel() throws PartakeResultException {
        return changeParticipationStatus(ParticipationStatus.CANCELLED, false, false);       
    }
    
    public String changeComment() throws PartakeResultException {
        return changeParticipationStatus(null, true, false);
    }

    
    public String enrollSubevent() throws PartakeResultException {
        return changeParticipationStatus(ParticipationStatus.ENROLLED, false, true);
    }
    
    public String reserveSubevent() throws PartakeResultException {
        return changeParticipationStatus(ParticipationStatus.RESERVED, false, true);
    }
    
    public String cancelSubevent() throws PartakeResultException {
        return changeParticipationStatus(ParticipationStatus.CANCELLED, false, true);
    }
    
    public String changeSubeventComment() throws PartakeResultException {
        return changeParticipationStatus(null, true, true);
    }
    
    public String twitterPromotion() {
        eventId = getParameter("eventId");
        String message = getParameter("message");
        if (eventId == null || message == null) { return INPUT; }
        
        UserEx user = getLoginUser();
        if (user == null) { return LOGIN; }
        
        try {
            event = EventService.get().getEventExById(eventId);
            if (event == null) { return NOT_FOUND; }
        } catch (DAOException e) {
            e.printStackTrace();
            addActionError("データベースに接続できません。");
            return ERROR;
        }

        // Only owner can promote the event.
        if (!event.hasPermission(user, UserPermission.EVENT_PROMOTE)) { return PROHIBITED; }

        AccessToken accessToken = new AccessToken(user.getTwitterLinkage().getAccessToken(),
                        user.getTwitterLinkage().getAccessTokenSecret());
        Twitter twitter = new TwitterFactory().getOAuthAuthorizedInstance(accessToken);

        try {
            twitter.updateStatus(message);
            return SUCCESS;
        } catch (TwitterException e) {
            e.printStackTrace();
            addActionError("twitter へのメッセージ送信時にエラーが発生しました。");
            return ERROR;
        }
    }

    public void validate() {
    }
    
    // ----------------------------------------------------------------------
    
    private String changeParticipationStatus(ParticipationStatus status, boolean changesOnlyComment, boolean enrollsSubevent) throws PartakeResultException {
        UserEx user = ensureLogin();
    	
	    String eventId = getParameter("eventId");
	    if (eventId == null) { return ERROR; }

	    // If the comment does not exist, we use empty string instead.
	    String comment = getParameter("comment");
	    if (comment == null) { comment = ""; } 
	    
	    try {
	        Event event = EventService.get().getEventById(eventId);
	        if (event == null) { return ERROR; }

	        // TODO: calculated deadline を使うように変更する。
	        Date deadline = event.getDeadline();
	        if (deadline == null) { deadline = event.getBeginDate(); }
	        
	        // もし、締め切りを過ぎている場合、変更が出来なくなる。
	        if (deadline.before(new Date())) {
	        	addActionError("締め切りを過ぎているため変更できません。");
	        	return ERROR;
	        }
	        
	        if (enrollsSubevent) { // if subevent.
//	            if (!EventService.get().enrollSubevent(user, event, status, comment, changesOnlyComment, event.isReservationTimeOver())) {
//	                // subevent への参加が許されなかった
//	                
//	            }
	        } else { // if main event.
    	        EventService.get().enroll(user, event, status, comment, changesOnlyComment, event.isReservationTimeOver());
    	        
    	        // Twitter で参加をつぶやく
    	        if (!changesOnlyComment) { tweetEnrollment(user, event, status); }
	        }
	        
	        this.eventId = eventId;
	        return SUCCESS;
	    } catch (DAOException e) {
	    	e.printStackTrace();
	        return ERROR;
	    }
    }

    private void tweetEnrollment(UserEx user, Event event, ParticipationStatus status) throws DAOException {
        String message;
        switch (status) {
        case ENROLLED:
        	message = "[PARTAKE] " + event.getTitle() + " (" + Util.bitlyShortURL(event.getEventURL()) + ") へ参加します。";
        	break;
        case RESERVED:
        	message = "[PARTAKE] " + event.getTitle() + " (" + Util.bitlyShortURL(event.getEventURL()) + ") へ参加予定です。";
        	break;
        case CANCELLED:
        	message = "[PARTAKE] " + event.getTitle() + " (" + Util.bitlyShortURL(event.getEventURL()) + ") への参加を取りやめました。";
        	break;
        default:
        	message = null;
        }
        
        UserPreference pref = UserService.get().getUserPreference(user.getId());
        if (message != null && pref != null && pref.tweetsAttendanceAutomatically()) {        	
        	DirectMessageService.get().tweetMessage(user, message);
        }
    }
}