package in.partake.controller.api.user;

import in.partake.base.Pair;
import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Event;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.auxiliary.CalculatedEnrollmentStatus;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetEnrollmentsAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    public String doExecute() throws DAOException, PartakeException {
        String userId = getValidUserIdParameter();

        int offset = optIntegerParameter("offset", 0);
        offset = Util.ensureRange(offset, 0, Integer.MAX_VALUE);

        int limit = optIntegerParameter("limit", 10);
        limit = Util.ensureRange(limit, 0, 100);

        GetEnrollmentsTransaction transaction = new GetEnrollmentsTransaction(userId, offset, limit);
        transaction.execute();

        JSONArray statuses = new JSONArray();
        for (Pair<Event, CalculatedEnrollmentStatus> eventStatus : transaction.getStatuses()) {
            if (eventStatus.getFirst().isPrivate() || eventStatus.getFirst().isPreview()) {
                // TODO: We should consider how to join EventEntities and EnrollmentEntities.
                // If the event is private (or draft), its information does not be fed.
                // However, we show the existence of the event now.
            } else {
                JSONObject obj = new JSONObject();
                obj.put("event", eventStatus.getFirst().toSafeJSON());
                obj.put("status", eventStatus.getSecond().toString());
                statuses.add(obj);
            }
        }

        JSONObject obj = new JSONObject();
        obj.put("numTotalEvents", transaction.getNumTotalEvents());
        obj.put("eventStatuses", statuses);

        return renderOK(obj);
    }
}

class GetEnrollmentsTransaction extends DBAccess<Void> {
    private String userId;
    private int offset;
    private int limit;

    private int numTotalEvents;
    private List<Pair<Event, CalculatedEnrollmentStatus>> statuses;

    public GetEnrollmentsTransaction(String userId, int offset, int limit) {
        this.userId = userId;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        // If |user| does not publish their events, return immediately.
        UserPreference pref = daos.getUserPreferenceAccess().find(con, userId);
        if (pref == null)
            pref = UserPreference.getDefaultPreference(userId);
        if (!pref.isProfilePublic())
            throw new PartakeException(UserErrorCode.INVALID_USER_PRIVATE);

        IEnrollmentAccess enrollmentAccess = daos.getEnrollmentAccess();
        List<Enrollment> enrollments = enrollmentAccess.findByUserId(con, userId, offset, limit);

        this.numTotalEvents = enrollmentAccess.countEventsByUserId(con, userId);
        this.statuses = new ArrayList<Pair<Event, CalculatedEnrollmentStatus>>();

        for (Enrollment enrollment : enrollments) {
            if (enrollment == null)
                continue;

            Event event = daos.getEventAccess().find(con, enrollment.getEventId());
            if (event == null)
                continue;

            CalculatedEnrollmentStatus calculatedEnrollmentStatus = EnrollmentDAOFacade.calculateEnrollmentStatus(con, daos, userId, event);
            statuses.add(new Pair<Event, CalculatedEnrollmentStatus>(event, calculatedEnrollmentStatus));
        }

        return null;
    }

    public int getNumTotalEvents() {
        return numTotalEvents;
    }

    public List<Pair<Event, CalculatedEnrollmentStatus>> getStatuses() {
        return this.statuses;
    }
}
