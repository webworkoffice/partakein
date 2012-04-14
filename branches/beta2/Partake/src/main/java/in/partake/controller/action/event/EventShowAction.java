package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.controller.base.permission.DraftEventEditPermission;
import in.partake.controller.base.permission.PrivateEventShowPermission;
import in.partake.model.CommentEx;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventEx;
import in.partake.model.EventMessageEx;
import in.partake.model.EventRelationEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.daofacade.MessageDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventReminder;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class EventShowAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    private EventEx event;
    private boolean needsPasscode;
    private List<Event> requiredEvents;
    private ParticipationStatus participationStatus;
    private ParticipationList participationList;
    private boolean deadlineOver;
    private List<CommentEx> comments;
    private List<EventMessageEx> eventMessages;
    private EventReminder eventReminder;
    private List<EventRelationEx> relations;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        String eventId = getValidEventIdParameter(UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        // NOTE: login はしてないかもしれない。
        UserEx user = getLoginUser();

        EventShowTransaction transaction = new EventShowTransaction(user, eventId, session);
        transaction.execute();

        event = transaction.getEvent();
        if (event == null)
            return renderNotFound();

        if (transaction.isNeedsPasscode()) {
            this.event = null;
            return renderRedirect("/events/passcode?eventId=" + eventId);
        }

        this.requiredEvents = transaction.getRequiredEvents();
        this.participationStatus = transaction.getParticipationStatus();
        this.participationList = transaction.getParticipationList();
        this.deadlineOver = transaction.isDeadlineOver();
        this.comments = transaction.getComments();
        this.eventMessages = transaction.getEventMessages();
        this.eventReminder = transaction.getEventReminder();
        this.relations = transaction.getRelations();

        return render("events/show.jsp");
    }

    public EventEx getEvent() {
        return event;
    }

    public boolean isNeedsPasscode() {
        return needsPasscode;
    }

    public List<Event> getRequiredEvents() {
        return requiredEvents;
    }

    public ParticipationStatus getParticipationStatus() {
        return participationStatus;
    }

    public ParticipationList getParticipationList() {
        return participationList;
    }

    public boolean isDeadlineOver() {
        return deadlineOver;
    }

    public List<CommentEx> getComments() {
        return comments;
    }

    public EventReminder getEventReminder() {
        return eventReminder;
    }

    public List<EventMessageEx> getEventMessages() {
        return eventMessages;
    }

    public List<EventRelationEx> getRelations() {
        return relations;
    }
}

class EventShowTransaction extends DBAccess<Void> {
    private UserEx user;
    private String eventId;
    private Map<String, Object> session; // Bad style...

    private EventEx event;
    private boolean needsPasscode;
    private List<Event> requiredEvents;
    private ParticipationStatus participationStatus;
    private ParticipationList participationList;
    private boolean deadlineOver;
    private List<CommentEx> comments;
    private List<EventMessageEx> eventMessages;
    private EventReminder eventReminder;
    private List<EventRelationEx> relations;

    public EventShowTransaction(UserEx user, String eventId, Map<String, Object> session) {
        this.user = user;
        this.eventId = eventId;
        this.session = session;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        event = EventDAOFacade.getEventEx(con, daos, eventId);
        if (event == null)
            return null;

        if (event.isPreview()) {
            // If the event is draft, only owner can see it.
            if (user == null || !DraftEventEditPermission.check(event, user))
                throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);
        }

        if (event.isPrivate()) {
            // owner および manager は見ることが出来る。
            String passcode = (String) session.get("event:" + eventId);
            if (user != null && PrivateEventShowPermission.check(event, user)) {
                // OK. You have the right to show this event.
            } else if (StringUtils.equals(event.getPasscode(), passcode)) {
                // OK. The same passcode.
            } else {
                // public でなければ、passcode を入れなければ見ることが出来ない
                // We make this.event null for foolproof.
                this.needsPasscode = true;
                return null;
            }
        }

        relations = EventDAOFacade.getEventRelationsEx(con, daos, event);

        // ----- 登録している、していないの条件を満たしているかどうかのチェック
        requiredEvents = EventDAOFacade.getRequiredEventsNotEnrolled(con, daos, user, relations);

        // ----- participants を反映
        List<EnrollmentEx> participations = EnrollmentDAOFacade.getEnrollmentExs(con, daos, event);
        if (participations == null)
            throw new PartakeException(ServerErrorCode.PARTICIPATIONS_RETRIEVAL_ERROR);

        participationList = event.calculateParticipationList(participations);

        Date deadline = event.getDeadline();
        if (deadline == null)
            deadline = event.getBeginDate();
        deadlineOver = deadline.before(new Date());

        comments = EventDAOFacade.getCommentsExByEvent(con, daos, eventId);
        eventMessages = MessageDAOFacade.findEventMessageExs(con, daos, eventId, 0, 100);

        if (user != null)
            participationStatus = EnrollmentDAOFacade.getParticipationStatus(con, daos, user.getId(), eventId);
        else
            participationStatus = ParticipationStatus.NOT_ENROLLED;

        eventReminder = daos.getEventReminderAccess().find(con, eventId);
        if (eventReminder == null)
            eventReminder = new EventReminder(eventId);

        return null;
    }

    public EventEx getEvent() {
        return event;
    }

    public boolean isNeedsPasscode() {
        return needsPasscode;
    }

    public List<Event> getRequiredEvents() {
        return requiredEvents;
    }

    public ParticipationStatus getParticipationStatus() {
        return participationStatus;
    }

    public ParticipationList getParticipationList() {
        return participationList;
    }

    public boolean isDeadlineOver() {
        return deadlineOver;
    }

    public List<CommentEx> getComments() {
        return comments;
    }

    public List<EventMessageEx> getEventMessages() {
        return eventMessages;
    }

    public EventReminder getEventReminder() {
        return eventReminder;
    }

    public List<EventRelationEx> getRelations() {
        return relations;
    }
}
