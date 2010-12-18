package in.partake.controller;

import in.partake.model.CommentEx;
import in.partake.model.DirectMessageEx;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.ParticipationEx;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dto.Comment;
import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.Event;
import in.partake.model.dto.ParticipationStatus;
import in.partake.model.dto.UserPermission;
import in.partake.model.dto.UserPreference;
import in.partake.resource.Constants;
import in.partake.resource.I18n;
import in.partake.service.DirectMessageService;
import in.partake.service.EventService;
import in.partake.service.MessageService;
import in.partake.service.UserService;
import in.partake.util.Util;

import java.util.ArrayList;
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
	/** */
	private static final long serialVersionUID = 1L;		
    private static final Logger logger = Logger.getLogger(EventsController.class);

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
	        
	        List<EventRelationEx> relations = EventService.get().getEventRelationsEx(eventId);
	        attributes.put(Constants.ATTR_EVENT_RELATIONS, relations);
	        
	        // ----- 登録している、していないの条件を満たしているかどうかのチェック
	        List<EventEx> requiredEvents = getRequiredEventsNotEnrolled(user, relations);
	        attributes.put(Constants.ATTR_REQUIRED_EVENTS, requiredEvents);
	        
	        // ----- participants を反映
	        // TODO: FIXME: 優先イベントへの参加の状況を反映させる必要がある。
	        // TODO: あれだ、リストを取ってくるところも同じように直さないとだめなんじゃなイカ？
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
	        List<DirectMessageEx> messages = DirectMessageService.get().getUserMessagesByEventId(eventId);
	        
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
	        attributes.put(Constants.ATTR_COMMENTSET, comments); // TODO: comment set DataIterator じゃなくて List だと思うんだよなあ...。本当は。
	        attributes.put(Constants.ATTR_MESSAGESET, messages);
	        attributes.put(Constants.ATTR_NOTIFICATION_STATUS, MessageService.get().getNotificationStatus(eventId));
	        
	        this.eventId = eventId;
	        this.event = event;
	        
	        return SUCCESS;
	    } catch (DAOException e) {
	    	e.printStackTrace();
	    	return ERROR;
	    }
    }
    
    // ----------------------------------------------------------------------
    // comment
    
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

    /**
     * comment を削除します。
     * @return
     * @throws PartakeResultException
     */
    public String removeComment() throws PartakeResultException {
    	UserEx user = ensureLogin();
    	if (user == null) { return LOGIN; }
    	
    	String commentId = getParameter("commentId");
    	if (commentId == null) { return ERROR; }
    	
    	this.eventId = getParameter("eventId");
    	if (eventId == null) { return ERROR; }
    	
    	try {
    		// これ Service にいれてしまわないといけないんじゃないかなー。transaction 的には。select for update というか。
    		// update conflict してもまあ問題ないのでいいか。
    		CommentEx comment = EventService.get().getCommentExById(commentId);
    		
    		// should be event owner or 
    		if (comment.getUser().getId().equals(user.getId()) || comment.getEvent().hasPermission(user, UserPermission.EVENT_REMOVE_COMMENT)) {
    			EventService.get().removeComment(commentId);
    			return SUCCESS;
    		} else {
    			addWarningMessage("イベント管理者もしくはコメントした本人だけがコメントを削除することが出来ます。");
    			return PROHIBITED;
    		}
    	} catch (DAOException e) {
    		String message = I18n.t(I18n.DATABASE_ERROR);
    		logger.error(message, e);
    		addWarningMessage(message);
    		return ERROR;
    	}
    	
    }
    
    // ----------------------------------------------------------------------
    // enroll 
    
    public String enroll() throws PartakeResultException {
    	return changeParticipationStatus(ParticipationStatus.ENROLLED, false);
    }
    
    public String reserve() throws PartakeResultException {
    	return changeParticipationStatus(ParticipationStatus.RESERVED, false);
    }
    
    public String cancel() throws PartakeResultException {
        return changeParticipationStatus(ParticipationStatus.CANCELLED, false);       
    }
    
    public String changeComment() throws PartakeResultException {
        return changeParticipationStatus(null, true);
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
    
    private String changeParticipationStatus(ParticipationStatus status, boolean changesOnlyComment) throws PartakeResultException {
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
	        
	        // 現在の状況が登録されていない場合、
	        List<EventRelationEx> relations = EventService.get().getEventRelationsEx(eventId);
	        ParticipationStatus currentStatus = UserService.get().getParticipationStatus(user, event);	        
	        if (!currentStatus.isEnrolled()) {
	        	List<EventEx> requiredEvents = getRequiredEventsNotEnrolled(user, relations); 
	        	if (requiredEvents != null && !requiredEvents.isEmpty()) {
	        		addActionError("登録必須のイベントがあるため参加登録が出来ません。");
	        		return ERROR;
	        	}
	        }
	        
	        EventService.get().enroll(user, event, status, comment, changesOnlyComment, event.isReservationTimeOver());
	        
	        // Twitter で参加をつぶやく
	        if (!changesOnlyComment) { tweetEnrollment(user, event, status); }
	        
	        this.eventId = eventId;
	        return SUCCESS;
	    } catch (DAOException e) {
	    	e.printStackTrace();
	        return ERROR;
	    }
    }

    private void tweetEnrollment(UserEx user, Event event, ParticipationStatus status) throws DAOException {    	
    	UserPreference pref = UserService.get().getUserPreference(user.getId());
    	if (pref == null || !pref.tweetsAttendanceAutomatically()) { return; }
    	
    	String left = "[PARTAKE] ";
    	String right;
        switch (status) {
        case ENROLLED:
        	right = " (" + Util.bitlyShortURL(event.getEventURL()) + ") へ参加します。";
        	break;
        case RESERVED:
        	right = " (" + Util.bitlyShortURL(event.getEventURL()) + ") へ参加予定です。";
        	break;
        case CANCELLED:
        	right = " (" + Util.bitlyShortURL(event.getEventURL()) + ") への参加を取りやめました。";
        	break;
        default:
        	right = null;
        }
        
        if (right == null) {
        	addWarningMessage("参加予定 tweet に失敗しました。");
        	return;
        }
        
        String message = left + Util.shorten(event.getTitle(), 140 - Util.codePointCount(left) - Util.codePointCount(right)) + right;
        DirectMessageService.get().tweetMessage(user, message);
    }
    
    /**
     * user が event に登録するために、登録が必要な event たちを列挙する。
     * @param eventId
     * @param user
     * @return
     * @throws DAOException
     */
	private List<EventEx> getRequiredEventsNotEnrolled(UserEx user, List<EventRelationEx> relations) throws DAOException {
		List<EventEx> requiredEvents = new ArrayList<EventEx>();
		for (EventRelationEx relation : relations) {
			if (!relation.isRequired()) { continue; }
			if (relation.getEvent() == null) { continue; }
			
			ParticipationStatus status = UserService.get().getParticipationStatus(user, relation.getEvent());
			if (status.isEnrolled()) { continue; }			
			requiredEvents.add(relation.getEvent());
		}
		
		return requiredEvents;
	}
}
