package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.controller.base.permission.DraftEventEditPermission;
import in.partake.controller.base.permission.PrivateEventShowPermission;
import in.partake.model.EventCommentEx;
import in.partake.model.UserTicketApplicationEx;
import in.partake.model.EventEx;
import in.partake.model.EventMessageEx;
import in.partake.model.EventRelationEx;
import in.partake.model.EventTicketHolderList;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.daofacade.MessageDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

public class EventShowAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    private EventEx event;
    private boolean needsPasscode;
    private List<Event> requiredEvents;
    private List<EventTicket> tickets;
    private Map<UUID, ParticipationStatus> participationStatusMap;
    private Map<UUID, EventTicketHolderList> ticketHolderListMap;
    private List<EventCommentEx> comments;
    private List<EventMessageEx> eventMessages;
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
        this.tickets = transaction.getEventTickets();
        this.participationStatusMap = transaction.getParticipationStatusMap();
        this.ticketHolderListMap = transaction.getTicketHolderListMap();
        this.comments = transaction.getComments();
        this.eventMessages = transaction.getEventMessages();
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

    public Map<UUID, ParticipationStatus> getParticipationStatusMap() {
        return participationStatusMap;
    }

    public Map<UUID, EventTicketHolderList>getTicketHolderListMap() {
        return ticketHolderListMap;
    }

    public List<EventCommentEx> getComments() {
        return comments;
    }

    public List<EventMessageEx> getEventMessages() {
        return eventMessages;
    }

    public List<EventRelationEx> getRelations() {
        return relations;
    }

    public List<EventTicket> getTickets() {
        return tickets;
    }
}

class EventShowTransaction extends DBAccess<Void> {
    private UserEx user;
    private String eventId;
    private Map<String, Object> session; // Bad style...

    private EventEx event;
    private boolean needsPasscode;
    private List<Event> requiredEvents;

    private Map<UUID, ParticipationStatus> participationStatusMap;
    private Map<UUID, EventTicketHolderList> ticketHolderListMap;

    private List<EventTicket> tickets;
    private List<EventCommentEx> comments;
    private List<EventMessageEx> eventMessages;
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

        if (event.isDraft()) {
            // If the event is draft, only owner can see it.
            if (user == null || !DraftEventEditPermission.check(event, user))
                throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);
        }

        if (!StringUtils.isBlank(event.getPasscode())) {
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
        tickets = daos.getEventTicketAccess().findEventTicketsByEventId(con, eventId);

        // ----- 登録している、していないの条件を満たしているかどうかのチェック
        requiredEvents = EventDAOFacade.getRequiredEventsNotEnrolled(con, daos, user, relations);

        // ----- participants を反映
        ticketHolderListMap = new HashMap<UUID, EventTicketHolderList>();
        participationStatusMap = new HashMap<UUID, ParticipationStatus>();
        for (EventTicket ticket : tickets) {
            List<UserTicketApplicationEx> participations = EnrollmentDAOFacade.getEnrollmentExs(con, daos, ticket, event);
            if (participations == null)
                throw new PartakeException(ServerErrorCode.PARTICIPATIONS_RETRIEVAL_ERROR);

            ticketHolderListMap.put(ticket.getId(), ticket.calculateParticipationList(event, participations));
            if (user != null)
                participationStatusMap.put(ticket.getId(), EnrollmentDAOFacade.getParticipationStatus(con, daos, user.getId(), ticket.getId()));
            else
                participationStatusMap.put(ticket.getId(), ParticipationStatus.NOT_ENROLLED);
        }

        comments = EventDAOFacade.getCommentsExByEvent(con, daos, eventId);
        eventMessages = MessageDAOFacade.findEventMessageExs(con, daos, eventId, 0, 100);

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

    public Map<UUID, ParticipationStatus> getParticipationStatusMap() {
        return participationStatusMap;
    }

    public Map<UUID, EventTicketHolderList>getTicketHolderListMap() {
        return ticketHolderListMap;
    }

    public List<EventCommentEx> getComments() {
        return comments;
    }

    public List<EventMessageEx> getEventMessages() {
        return eventMessages;
    }

    public List<EventTicket> getEventTickets() {
        return tickets;
    }

    public List<EventRelationEx> getRelations() {
        return relations;
    }
}
