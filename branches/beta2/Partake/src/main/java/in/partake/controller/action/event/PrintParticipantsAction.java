package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.controller.base.permission.EventParticipationListPermission;
import in.partake.model.UserTicketApplicationEx;
import in.partake.model.EventEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.EventTicketHolderList;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.EventTicket;
import in.partake.resource.UserErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PrintParticipantsAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    private EventEx event;
    private Map<UUID, EventTicketHolderList> ticketHolderListMap;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        String eventId = getValidEventIdParameter();

        ParticipantsListTransaction transaction = new ParticipantsListTransaction(user, eventId);
        transaction.execute();

        event = transaction.getEvent();
        ticketHolderListMap = transaction.getTicketHolderListMap();

        return render("events/participants/print.jsp");
    }

    public EventEx getEvent() {
        return event;
    }

    public Map<UUID, EventTicketHolderList> getTicketHolderListMap() {
        return ticketHolderListMap;
    }
}

class ParticipantsListTransaction extends DBAccess<Void> {
    private UserEx user;
    private String eventId;

    private EventEx event;
    private List<EventTicket> tickets;
    private Map<UUID, EventTicketHolderList> ticketHolderListMap;

    public ParticipantsListTransaction(UserEx user, String eventId) {
        this.user = user;
        this.eventId = eventId;
        this.ticketHolderListMap = new HashMap<UUID, EventTicketHolderList>();
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        event = EventDAOFacade.getEventEx(con, daos, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_NOTFOUND);

        // Only owner can retrieve the participants list.
        if (!EventParticipationListPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);

        tickets = daos.getEventTicketAccess().findEventTicketsByEventId(con, eventId);
        for (EventTicket ticket : tickets) {
            List<UserTicketApplicationEx> participations = EnrollmentDAOFacade.getEnrollmentExs(con, daos, ticket, event);
            ticketHolderListMap.put(ticket.getId(), ticket.calculateParticipationList(event, participations));
        }

        return null;
    }

    public EventEx getEvent() {
        return event;
    }

    public List<EventTicket> getTickets() {
        return tickets;
    }

    public Map<UUID, EventTicketHolderList> getTicketHolderListMap() {
        return ticketHolderListMap;
    }
}
