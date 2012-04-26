package in.partake.controller.api.account;

import in.partake.base.Pair;
import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTicketAccess;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.CalculatedEnrollmentStatus;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetTicketsAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    public String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();

        int offset = optIntegerParameter("offset", 0);
        offset = Util.ensureRange(offset, 0, Integer.MAX_VALUE);

        int limit = optIntegerParameter("limit", 10);
        limit = Util.ensureRange(limit, 0, 100);

        GetEnrollmentsTransaction transaction = new GetEnrollmentsTransaction(user.getId(), offset, limit);
        transaction.execute();

        JSONArray statuses = new JSONArray();
        for (Pair<EventTicket, CalculatedEnrollmentStatus> ticketAndStatus : transaction.getStatuses()) {
            JSONObject obj = new JSONObject();
            obj.put("ticket", ticketAndStatus.getFirst().toSafeJSON());
            obj.put("status", ticketAndStatus.getSecond().toString());
            statuses.add(obj);
        }

        JSONObject obj = new JSONObject();
        obj.put("totalTicketCount", transaction.getNumTotalTickets());
        obj.put("ticketStatuses", statuses);

        return renderOK(obj);
    }
}

class GetEnrollmentsTransaction extends DBAccess<Void> {
    private String userId;
    private int offset;
    private int limit;

    private int numTotalTickets;
    private List<Pair<EventTicket, CalculatedEnrollmentStatus>> statuses;

    public GetEnrollmentsTransaction(String userId, int offset, int limit) {
        this.userId = userId;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IUserTicketAccess enrollmentAccess = daos.getEnrollmentAccess();
        List<UserTicket> enrollments = enrollmentAccess.findByUserId(con, userId, offset, limit);

        this.numTotalTickets = enrollmentAccess.countByUserId(con, userId);
        this.statuses = new ArrayList<Pair<EventTicket, CalculatedEnrollmentStatus>>();

        for (UserTicket enrollment : enrollments) {
            if (enrollment == null)
                continue;

            Event event = daos.getEventAccess().find(con, enrollment.getEventId());
            if (event == null)
                continue;

            EventTicket ticket = daos.getEventTicketAccess().find(con, enrollment.getTicketId());
            if (ticket == null)
                continue;

            CalculatedEnrollmentStatus calculatedEnrollmentStatus = EnrollmentDAOFacade.calculateEnrollmentStatus(con, daos, userId, ticket, event);
            statuses.add(new Pair<EventTicket, CalculatedEnrollmentStatus>(ticket, calculatedEnrollmentStatus));
        }

        return null;
    }

    public int getNumTotalTickets() {
        return numTotalTickets;
    }

    public List<Pair<EventTicket, CalculatedEnrollmentStatus>> getStatuses() {
        return this.statuses;
    }
}
