package in.partake.controller.api.account;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.EventParticipation;
import in.partake.model.EventParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.daofacade.deprecated.UserService;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GetEventsAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    public String doExecute() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        // TODO: queryType is either of:
        //    draft, owner, manager, editor, participants.
        // The default value is participants. 
        //    manager means owner or editor. (?)
        // TODO: finished is either of:
        //    finished, unfinished, all
        // The default value is all.

        // TODO: Should be refactored, and the code should be moved to EventService.
        // currently we accept only:
        //    draft/all
        //    manager/all
        //    participants/false
        //    participants/finished

        String queryType = getParameter("queryType");
        String finished = getParameter("finished");

        Integer limit = getIntegerParameter("limit");
        if (limit == null || limit < 0 || 100 < limit)
            limit = 10;

        Integer offset = getIntegerParameter("offset");
        if (offset == null || offset < 0 || 100 < offset)
            offset = 10;

        List<Event> events = new ArrayList<Event>();

        if ("draft".equalsIgnoreCase(queryType) && "all".equalsIgnoreCase(finished)) {
            events.addAll(EventService.get().getDraftEvents(user.getId()));
        } else if ("manager".equalsIgnoreCase(queryType) && "all".equalsIgnoreCase(finished)) {
            // FIXME: 自分自身が manager に含まれていたら２つでる
            events.addAll(EventService.get().getEventsOwnedBy(user));
            events.addAll(EventService.get().getEventsManagedBy(user));
        } else if ("participants".equalsIgnoreCase(queryType) && "false".equalsIgnoreCase(finished)) {
            List<Event> enrolledEvents = UserService.get().getEnrolledEvents(user.getId());
            Date now = new Date();
            for (Event e : enrolledEvents) {
                if (e == null) { continue; }
                if (!e.getBeginDate().before(now))
                    events.add(e);
            }

        } else if ("participants".equalsIgnoreCase(queryType) && "true".equalsIgnoreCase(finished)) {
            List<Event> enrolledEvents = UserService.get().getEnrolledEvents(user.getId());
            Date now = new Date();
            for (Event e : enrolledEvents) {
                if (e == null) { continue; }
                if (e.getBeginDate().before(now))
                    events.add(e);
            }
        } else {
            return renderInvalid(UserErrorCode.INVALID_ARGUMENT);
        }

        Collections.sort(events, Event.getComparatorBeginDateAsc());

        if (offset >= events.size())
            events = Collections.emptyList();
        else
            events = events.subList(offset, offset + limit <= events.size() ? offset + limit : events.size());

        ArrayList<EventParticipation> participations = new ArrayList<EventParticipation>();
        for (Event event : events) {
            if (event == null)
                continue;
            int numUsers = EventService.get().getNumOfEnrolledUsers(event.getId());
            ParticipationStatus status = UserService.get().getParticipationStatus(user.getId(), event.getId());
            participations.add(new EventParticipation(event, numUsers, status));
        }

        EventParticipationList list = new EventParticipationList(participations, events.size());
        return renderOK(list.toSafeJSON());
    }
}
