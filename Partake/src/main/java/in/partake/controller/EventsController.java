package in.partake.controller;

import in.partake.base.Util;
import in.partake.controller.base.PartakeResultException;
import in.partake.model.CommentEx;
import in.partake.model.DirectMessageEx;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.EnrollmentEx;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.daofacade.deprecated.MessageService;
import in.partake.model.daofacade.deprecated.UserService;
import in.partake.model.dto.Comment;
import in.partake.model.dto.Event;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.Constants;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

public class EventsController extends DeprecatedPartakeActionSupport {
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
    @SkipValidation
    public String show() {
    	String eventId = getParameter("eventId");
        if (eventId == null) { return NOT_FOUND; }
        if (!Util.isUUID(eventId)) { return NOT_FOUND; }
        
     	// NOTE: login はしてないかもしれない。
        UserEx user = getLoginUser();

	    try {
	        EventEx event = EventService.get().getEventExById(eventId);
	        if (event == null) {
	            try {
	                if (EventService.get().isRemoved(eventId)) return "removed";
	            } catch (DAOException ignore) {
	                logger.warn("DAOException occured", ignore);
	            }
	            return NOT_FOUND;
	        }

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
		        	return "passcode"; // passcode required.
	        	}
	        }

	        List<EventRelationEx> relations = EventService.get().getEventRelationsEx(eventId);
	        attributes.put(Constants.ATTR_EVENT_RELATIONS, relations);

	        // ----- 登録している、していないの条件を満たしているかどうかのチェック
	        List<Event> requiredEvents = getRequiredEventsNotEnrolled(user, relations);
	        attributes.put(Constants.ATTR_REQUIRED_EVENTS, requiredEvents);

	        // ----- participants を反映
	        List<EnrollmentEx> participations = EventService.get().getEnrollmentEx(event.getId());
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


	        List<CommentEx> comments = EventService.get().getCommentsExByEvent(eventId);
	        List<DirectMessageEx> messages = MessageService.get().getUserMessagesByEventId(eventId);



	        attributes.put(Constants.ATTR_EVENT, event);
	        attributes.put(Constants.ATTR_PARTICIPATIONLIST, participationList);

	        if (user != null) {
	        	attributes.put(Constants.ATTR_PARTICIPATION_STATUS, UserService.get().getParticipationStatus(user.getId(), event.getId()));
	        } else {
	        	attributes.put(Constants.ATTR_PARTICIPATION_STATUS, ParticipationStatus.NOT_ENROLLED);
	        }
	        attributes.put(Constants.ATTR_DEADLINE_OVER, Boolean.valueOf(deadlineOver));
	        attributes.put(Constants.ATTR_COMMENTSET, comments);
	        attributes.put(Constants.ATTR_MESSAGESET, messages);
	        attributes.put(Constants.ATTR_REMINDER_STATUS, MessageService.get().getReminderStatus(eventId));

	        this.eventId = eventId;
	        this.event = event;

	        if (event.hasPermission(user, UserPermission.EVENT_SEND_MESSAGE)) {
	        	Integer restCodePoints = MessageService.get().calcRestCodePoints(user, event);
	        	attributes.put(Constants.ATTR_MAX_CODE_POINTS_OF_MESSAGE, restCodePoints);
	        } else {
	            attributes.put(Constants.ATTR_MAX_CODE_POINTS_OF_MESSAGE, 0);
	        }

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
   			return INVALID;
   		}

   		try {
            event = EventService.get().getEventExById(eventId);
            if (event == null) {
                logger.warn("EVENT ID : "  + eventId + " was not found.");
                return INVALID;
            }

	   		String comment = getParameter("comment");
	   		if (StringUtils.isEmpty(comment)) {
	   		    addWarningMessage("コメントを入力してください。");
	   		    return INPUT;
	   		}

	   		Comment embryo = new Comment(eventId, user.getId(), comment, true, new Date());
	   		EventService.get().addComment(embryo);

	   		return SUCCESS;
    	} catch (DAOException e){
    		e.printStackTrace();
    		return ERROR;
    	}
    }

    // ----------------------------------------------------------------------
    // enroll

    public String enroll() throws PartakeResultException, DAOException {
    	return changeParticipationStatus(ParticipationStatus.ENROLLED, false);
    }

    public String reserve() throws PartakeResultException, DAOException {
    	return changeParticipationStatus(ParticipationStatus.RESERVED, false);
    }

    public String cancel() throws PartakeResultException, DAOException {
        return changeParticipationStatus(ParticipationStatus.CANCELLED, false);
    }

    public String changeComment() throws PartakeResultException, DAOException {
        return changeParticipationStatus(null, true);
    }

    public String changeEnrollment() throws PartakeResultException, DAOException {
        UserEx user = ensureLogin();
        if (user == null) { return ERROR; }

        String eventId = getParameter("eventId");
        if (StringUtils.isEmpty(eventId)) { return ERROR; }

        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null) { return ERROR; }

        throw new RuntimeException("Not implemented yet.");
    }

    // ----------------------------------------------------------------------

    private String changeParticipationStatus(ParticipationStatus status, boolean changesOnlyComment) throws PartakeResultException, DAOException {
        UserEx user = ensureLogin();

	    String eventId = getParameter("eventId");
	    if (eventId == null) { return renderInvalid(UserErrorCode.MISSING_EVENT_ID); }

	    this.eventId = eventId;

	    // If the comment does not exist, we use empty string instead.
	    String comment = getParameter("comment");
	    if (comment == null) { comment = ""; }
	    if (comment.length() > 1024) {
	        addWarningMessage("コメントが長すぎます。");
	        return INPUT;
	    }

        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null) { return ERROR; }

        Date deadline = event.getCalculatedDeadline();

        // もし、締め切りを過ぎている場合、変更が出来なくなる。
        if (deadline.before(new Date())) {
        	addActionError("締め切りを過ぎているため変更できません。");
        	return ERROR;
        }

        // 現在の状況が登録されていない場合、
        List<EventRelationEx> relations = EventService.get().getEventRelationsEx(eventId);
        ParticipationStatus currentStatus = UserService.get().getParticipationStatus(user.getId(), event.getId());
        if (!currentStatus.isEnrolled()) {
        	List<Event> requiredEvents = getRequiredEventsNotEnrolled(user, relations);
        	if (requiredEvents != null && !requiredEvents.isEmpty()) {
        		addActionError("登録必須のイベントがあるため参加登録が出来ません。");
        		return ERROR;
        	}
        }

        EventService.get().enroll(user.getId(), event.getId(), status, comment, changesOnlyComment, event.isReservationTimeOver());

        // Twitter で参加をつぶやく
        if (!changesOnlyComment) { tweetEnrollment(user, event, status); }

        return SUCCESS;
    }

    private void tweetEnrollment(UserEx user, EventEx event, ParticipationStatus status) throws DAOException {
    	UserPreference pref = UserService.get().getUserPreference(user.getId());
    	if (pref == null || !pref.tweetsAttendanceAutomatically()) { return; }

    	String left = "[PARTAKE] ";
    	String right;
        switch (status) {
        case ENROLLED:
        	right = " (" + event.getShortenedURL() + ") へ参加します。";
        	break;
        case RESERVED:
        	right = " (" + event.getShortenedURL() + ") へ参加予定です。";
        	break;
        case CANCELLED:
        	right = " (" + event.getShortenedURL() + ") への参加を取りやめました。";
        	break;
        default:
        	right = null;
        }

        if (right == null) {
        	addWarningMessage("参加予定 tweet に失敗しました。");
        	return;
        }

        String message = left + Util.shorten(event.getTitle(), 140 - Util.codePointCount(left) - Util.codePointCount(right)) + right;
        MessageService.get().tweetMessage(user, message);
    }

    /**
     * user が event に登録するために、登録が必要な event たちを列挙する。
     * @param eventId
     * @param user
     * @return
     * @throws DAOException
     */
	private List<Event> getRequiredEventsNotEnrolled(UserEx user, List<EventRelationEx> relations) throws DAOException {
		List<Event> requiredEvents = new ArrayList<Event>();
		for (EventRelationEx relation : relations) {
			if (!relation.isRequired()) { continue; }
			if (relation.getEvent() == null) { continue; }
			if (user != null) {
    			ParticipationStatus status = UserService.get().getParticipationStatus(user.getId(), relation.getEvent().getId());
    			if (status.isEnrolled()) { continue; }
			}
			requiredEvents.add(relation.getEvent());
		}

		return requiredEvents;
	}
}
