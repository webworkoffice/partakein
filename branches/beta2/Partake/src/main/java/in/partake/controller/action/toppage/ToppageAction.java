package in.partake.controller.action.toppage;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.aux.EventFilterCondition;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.Event;
import in.partake.service.IEventSearchService;

import java.util.ArrayList;
import java.util.List;

public class ToppageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    private List<Event> recentEvents;
    private List<Event> ownedEvents;
    private List<Event> enrolledEvents;

    protected String doExecute() throws DAOException, PartakeException {
        // もしログインしていれば、最近のイベントを表示する。
        UserEx user = getLoginUser();

        ToppageTransaction transaction = new ToppageTransaction(user);
        transaction.execute();

        recentEvents = transaction.getRecentEvents();
        ownedEvents = transaction.getOwnedEvents();
        enrolledEvents = transaction.getEnrolledEvents();

        return render("index.jsp");
    }

    public List<Event> getRecentEvents() {
        return this.recentEvents;
    }

    public List<Event> getOwnedEvents() {
        return this.ownedEvents;
    }

    public List<Event> getEnrolledEvents() {
        return this.enrolledEvents;
    }
}

class ToppageTransaction extends DBAccess<Void> {
    private static final int NUM_EVENTS_TO_DISPLAY = 10;

    private UserEx user;
    private List<Event> recentEvents;
    private List<Event> ownedEvents;
    private List<Event> enrolledEvents;

    public ToppageTransaction(UserEx user) {
        this.user = user;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IEventSearchService searchService = PartakeApp.getEventSearchService();
        List<String> eventIds = searchService.getRecent(NUM_EVENTS_TO_DISPLAY);

        recentEvents = new ArrayList<Event>();
        for (String eventId : eventIds) {
            Event event = daos.getEventAccess().find(con, eventId);
            if (event != null)
                recentEvents.add(event);
        }

        if (user != null) {
            ownedEvents = daos.getEventAccess().findByOwnerId(con, user.getId(), EventFilterCondition.ALL_EVENTS, 0, 5);

            enrolledEvents = new ArrayList<Event>();
            List<UserTicket> enrollments = daos.getEnrollmentAccess().findByUserId(con, user.getId(), 0, 5);
            for (UserTicket enrollment : enrollments) {
                Event event = daos.getEventAccess().find(con, enrollment.getEventId());
                if (event != null)
                    enrolledEvents.add(event);
            }
        }

        return null;
    }

    public List<Event> getRecentEvents() {
        return this.recentEvents;
    }

    public List<Event> getOwnedEvents() {
        return this.ownedEvents;
    }

    public List<Event> getEnrolledEvents() {
        return this.enrolledEvents;
    }
}
