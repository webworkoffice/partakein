package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.model.CommentEx;
import in.partake.model.DirectMessageEx;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.daofacade.deprecated.MessageService;
import in.partake.model.daofacade.deprecated.UserService;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.resource.Constants;
import in.partake.resource.ServerErrorCode;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class EventShowAction extends AbstractEventAction {
    private static final long serialVersionUID = 1L;

    protected EventEx event;
    
    @Override
    protected String doExecute() throws DAOException, PartakeException {
        String eventId = getValidEventIdParameter();

        // NOTE: login はしてないかもしれない。
        UserEx user = getLoginUser();

        event = EventService.get().getEventExById(eventId);        
        if (event == null) {
            if (EventService.get().isRemoved(eventId))
                return render("events/removed.jsp");
            return renderNotFound();
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
                return renderRedirect("/events/passcode?eventId=" + eventId);
            }
        }

        List<EventRelationEx> relations = EventService.get().getEventRelationsEx(eventId);
        attributes.put(Constants.ATTR_EVENT_RELATIONS, relations);

        // ----- 登録している、していないの条件を満たしているかどうかのチェック
        List<Event> requiredEvents = getRequiredEventsNotEnrolled(user, relations);
        attributes.put(Constants.ATTR_REQUIRED_EVENTS, requiredEvents);

        // ----- participants を反映
        List<EnrollmentEx> participations = EventService.get().getEnrollmentEx(event.getId());
        if (participations == null)
            return renderError(ServerErrorCode.PARTICIPATIONS_RETRIEVAL_ERROR);

        ParticipationList participationList = event.calculateParticipationList(participations);

        Date deadline = event.getDeadline();
        if (deadline == null)
            deadline = event.getBeginDate();
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

        if (event.hasPermission(user, UserPermission.EVENT_SEND_MESSAGE)) {
            Integer restCodePoints = MessageService.get().calcRestCodePoints(user, event);
            attributes.put(Constants.ATTR_MAX_CODE_POINTS_OF_MESSAGE, restCodePoints);
        } else {
            attributes.put(Constants.ATTR_MAX_CODE_POINTS_OF_MESSAGE, 0);
        }

        return render("events/show.jsp");
    }
    
    public EventEx getEvent() { 
        return event;
    }
}
