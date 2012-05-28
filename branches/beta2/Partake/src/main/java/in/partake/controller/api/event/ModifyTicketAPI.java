package in.partake.controller.api.event;

import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventTicketAccess;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.EnqueteAnswerType;
import in.partake.model.dto.auxiliary.EnqueteQuestion;
import in.partake.model.dto.auxiliary.TicketApplicationEnd;
import in.partake.model.dto.auxiliary.TicketApplicationStart;
import in.partake.model.dto.auxiliary.TicketPriceType;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.postgresql.jdbc2.TimestampUtils;

import net.sf.json.JSONArray;

public class ModifyTicketAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String eventId = getValidEventIdParameter();

        String[] ids = ensureParameters("id[]", UserErrorCode.INVALID_ARGUMENT);
        int N = ids.length;

        String[] names = ensureParameters("name[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] startDateTypes = ensureParameters("startDateType[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] startDateDays = ensureParameters("startDateDays[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] customStartDates = ensureParameters("customStartDate[]", N, UserErrorCode.INVALID_ARGUMENT);

        String[] endDateTypes = ensureParameters("endDateType[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] endDateDays = ensureParameters("endDateDays[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] customEndDates = ensureParameters("customEndDate[]", N, UserErrorCode.INVALID_ARGUMENT);

        String[] priceTypes = ensureParameters("priceTypes[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] prices = ensureParameters("price[]", N, UserErrorCode.INVALID_ARGUMENT);

        String[] amountInfinites = ensureParameters("amountInfinite[]", N, UserErrorCode.INVALID_ARGUMENT);
        String[] amounts = ensureParameters("amount[]", N, UserErrorCode.INVALID_ARGUMENT);

        List<EventTicket> tickets = new ArrayList<EventTicket>();
        try {
            for (int i = 0; i < N; ++i) {
                if (!Util.isUUID(ids[i]))
                    return renderInvalid(UserErrorCode.INVALID_ARGUMENT);
                UUID id = UUID.fromString(ids[i]);
                EventTicket ticket = new EventTicket(id, eventId, i, names[i],
                        TicketApplicationStart.safeValueOf(startDateTypes[i]), Integer.parseInt(startDateDays[i]), TimeUtil.parseForEvent(customStartDates[i]),
                        TicketApplicationEnd.safeValueOf(endDateTypes[i]), Integer.parseInt(endDateDays[i]), TimeUtil.parseForEvent(customEndDates[i]),
                        TicketPriceType.safeValueOf(priceTypes[i]), Integer.parseInt(prices[i]),
                        "true".equals(amountInfinites[i]), Integer.parseInt(amounts[i]),
                        TimeUtil.getCurrentDateTime(), TimeUtil.getCurrentDateTime());

                // TODO: Check ticket validity.
                if (!ticket.validate())
                    return renderInvalid(UserErrorCode.INVALID_ARGUMENT);

                tickets.add(ticket);
            }
        } catch (NumberFormatException e) {
            return renderInvalid(UserErrorCode.INVALID_ARGUMENT);
        }

        new ModifyTicketTransaction(user, eventId, tickets).execute();
        return renderOK();
    }
}

class ModifyTicketTransaction extends Transaction<Void> {
    private UserEx user;
    private String eventId;
    private List<EventTicket> tickets;

    public ModifyTicketTransaction(UserEx user, String eventId, List<EventTicket> tickets) {
        this.user = user;
        this.eventId = eventId;
        this.tickets = tickets;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        Event event = daos.getEventAccess().find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);
        if (!EventEditPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        if (event.isDraft())
            modifyTicketsForDraftEvent(con, daos);
        else
            modifyTicketsForPublishedEvent(con, daos);

        return null;
    }

    private void modifyTicketsForDraftEvent(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        // Replaces all the tickets.
        IEventTicketAccess dao = daos.getEventTicketAccess();
        dao.removeByEventId(con, eventId);
        for (EventTicket ticket : tickets)
            dao.put(con, ticket);
    }

    private void modifyTicketsForPublishedEvent(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IEventTicketAccess dao = daos.getEventTicketAccess();
        List<EventTicket> originalTickets = dao.findEventTicketsByEventId(con, eventId);
        boolean[] processed = new boolean[originalTickets.size()];

        // |tickets| should contain all the original ticket.
        for (EventTicket ticket : tickets) {
            boolean found = false;
            for (int i = 0; i < originalTickets.size(); ++i) {
                if (!ticket.getId().equals(originalTickets.get(i).getId()))
                    continue;
                if (processed[i])
                    throw new PartakeException(UserErrorCode.INVALID_TICKET_DUPLICATE_ID);

                // Found the original ticket.
                processed[i] = true;
                found = true;

                // Capacity cannot be reduced.
                // TODO: We should


                break;
            }

            if (!found && ticket.getId() != null)
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS);


        }

    }
}
