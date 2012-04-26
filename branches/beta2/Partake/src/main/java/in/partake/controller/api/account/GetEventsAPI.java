package in.partake.controller.api.account;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTicketAccess;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.aux.EventFilterCondition;
import in.partake.model.dao.aux.EventStatus;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetEventsAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    public String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();

        String queryType = getParameter("queryType");

        int offset = optIntegerParameter("offset", 0);
        offset = Util.ensureRange(offset, 0, Integer.MAX_VALUE);

        int limit = optIntegerParameter("limit", 10);
        limit = Util.ensureRange(limit, 0, 100);

        GetEventsTransaction transaction = new GetEventsTransaction(user, queryType, offset, limit);
        transaction.execute();

        JSONArray statuses = new JSONArray();
        for (EventStatus status : transaction.getEventStatuses())
            statuses.add(status.toSafeJSON());

        JSONObject obj = new JSONObject();
        obj.put("numTotalEvents", transaction.getNumTotalEvents());
        obj.put("eventStatuses", statuses);

        return renderOK(obj);
    }
}

// TODO: We should not read all events here.
class GetEventsTransaction extends DBAccess<Void> {
    // TODO: Since we use 'screenname' to check editor's privileges, we have to have UserEx here.
    // We should have only userId here.
    private UserEx user;
    private String queryType;
    private int offset;
    private int limit;

    private List<Event> eventsRetrieved;

    private int numTotalEvents;
    private List<EventStatus> eventStatuses;

    public GetEventsTransaction(UserEx user, String queryType, int offset, int limit) {
        this.user = user;
        this.queryType = queryType;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        getEventsFromDB(con, daos);

        IUserTicketAccess enrollmentAccess = daos.getEnrollmentAccess();

        this.eventStatuses = new ArrayList<EventStatus>();
        for (Event event : eventsRetrieved) {
            if (event == null)
                continue;

            List<EventTicket> tickets = daos.getEventTicketAccess().findEventTicketsByEventId(con, event.getId());
            boolean isBeforeDeadline = event.acceptsSomeTicketsTill(tickets).isBefore(TimeUtil.getCurrentDateTime());
            int numEnrolledUsers = enrollmentAccess.countByEventId(con, event.getId(), ParticipationStatus.ENROLLED);
            int numReservedUsers = enrollmentAccess.countByEventId(con, event.getId(), ParticipationStatus.RESERVED);
            int numCancelledUsers = enrollmentAccess.countByEventId(con, event.getId(), ParticipationStatus.CANCELLED);
            eventStatuses.add(new EventStatus(event, isBeforeDeadline, numEnrolledUsers, numReservedUsers, numCancelledUsers));
        }

        return null;
    }

    private void getEventsFromDB(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IEventAccess eventDao = daos.getEventAccess();

        if ("owner".equalsIgnoreCase(queryType)) {
            this.numTotalEvents = eventDao.countEventsByOwnerId(con, user.getId(), EventFilterCondition.PUBLISHED_EVENT_ONLY);
            this.eventsRetrieved = eventDao.findByOwnerId(con, user.getId(), EventFilterCondition.PUBLISHED_EVENT_ONLY, offset, limit);
        } else if ("draft".equalsIgnoreCase(queryType)) {
            this.numTotalEvents = eventDao.countEventsByOwnerId(con, user.getId(), EventFilterCondition.DRAFT_EVENT_ONLY);
            this.eventsRetrieved = eventDao.findByOwnerId(con, user.getId(), EventFilterCondition.DRAFT_EVENT_ONLY, offset, limit);
        } else if ("editor".equalsIgnoreCase(queryType)) {
            this.numTotalEvents = eventDao.countEventsByScreenName(con, user.getScreenName(), EventFilterCondition.PUBLISHED_EVENT_ONLY);
            this.eventsRetrieved = eventDao.findByScreenName(con, user.getScreenName(), EventFilterCondition.PUBLISHED_EVENT_ONLY, offset, limit);
        } else {
            throw new PartakeException(UserErrorCode.INVALID_ARGUMENT);
        }
    }

    public int getNumTotalEvents() {
        return numTotalEvents;
    }

    public List<EventStatus> getEventStatuses() {
        return this.eventStatuses;
    }
}