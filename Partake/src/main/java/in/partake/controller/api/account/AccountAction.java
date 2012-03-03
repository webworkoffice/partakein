package in.partake.controller.api.account;

import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.EventParticipation;
import in.partake.model.EventParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.Constants;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.service.CalendarService;
import in.partake.service.EventService;
import in.partake.service.UserService;
import in.partake.session.PartakeSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

public class AccountAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;
    // private static final Logger logger = Logger.getLogger(AccountAction.class);

    public String getSessionToken() throws DAOException {
        PartakeSession session = getPartakeSession();
        if (session == null)
            return renderInvalid(UserErrorCode.MISSING_SESSION);

        if (session.getCSRFPrevention() == null)
            return renderError(ServerErrorCode.NO_CSRF_PREVENTION);

        if (session.getCSRFPrevention().getSessionToken() == null)
            return renderError(ServerErrorCode.NO_CREATED_SESSION_TOKEN);

        JSONObject obj = new JSONObject();
        obj.put("token", session.getCSRFPrevention().getSessionToken());

        return renderOK(obj);
    }

    public String get() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        JSONObject obj = user.toSafeJSON();

        UserPreference pref = UserService.get().getUserPreference(user.getId());
        if (pref != null)
            obj.put("preference", pref.toSafeJSON());

        List<String> openIds = UserService.get().getOpenIDIdentifiers(user.getId());
        if (openIds != null)
            obj.put("openId", openIds);

        return renderOK(obj);
    }

    // TODO: should be re-implemented.
    public String getEvents() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        // TODO: queryType is either of:
        //    owner, manager, editor, participants.
        // The default value is participants. 
        //    manager means owner or editor. (?)
        // TODO: finished is either of:
        //    finished, unfinished, all
        // The default value is all.

        // TODO: Should be refactored, and the code should be moved to EventService.
        // currently we accept only:
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

        if ("manager".equalsIgnoreCase(queryType) && "all".equalsIgnoreCase(finished)) {
            // TODO: 自分自身が manager に含まれていたら２つでる
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

    public String setPreference() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        if (!checkSessionToken())
            return renderInvalid(UserErrorCode.INVALID_SESSION);

        Boolean profilePublic = getBooleanParameter("profilePublic");
        Boolean receivingTwitterMessage = getBooleanParameter("receivingTwitterMessage");
        Boolean tweetingAttendanceAutomatically = getBooleanParameter("tweetingAttendanceAutomatically");

        UserService.get().updateUserPreference(user.getId(), profilePublic, receivingTwitterMessage, tweetingAttendanceAutomatically);

        return renderOK();
    }

    public String removeOpenID() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        if (!checkSessionToken())
            return renderInvalid(UserErrorCode.INVALID_SESSION);

        // check arguments
        String identifier = getParameter("identifier");
        if (identifier == null)
            return renderInvalid(UserErrorCode.MISSING_OPENID);

        // identifier が user と結び付けられているか検査して消去
        if (UserService.get().removeOpenIDLinkage(user.getId(), identifier))
            return renderOK();
        else
            return renderInvalid(UserErrorCode.INVALID_OPENID);
    }

    public String revokeCalendar() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        if (!checkSessionToken())
            return renderInvalid(UserErrorCode.INVALID_SESSION);

        String newCalendarId = CalendarService.get().revokeCalendar(user);

        // TODO: Unfortunately, the [user] must be updated to reflect this calendar revocation.
        // For convenient way, we retrieve user again, and set it to the session.           
        user = UserService.get().getUserExById(user.getId());
        session.put(Constants.ATTR_USER, user);

        JSONObject obj = new JSONObject();
        obj.put("calendarId", newCalendarId);

        return renderOK(obj);
    }
}
