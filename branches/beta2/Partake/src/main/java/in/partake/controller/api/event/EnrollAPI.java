package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.daofacade.deprecated.MessageService;
import in.partake.model.daofacade.deprecated.UserService;
import in.partake.model.dto.Event;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.UserErrorCode;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

public class EnrollAPI extends AbstractEventAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws PartakeException, DAOException {
        String status = getParameter("status");
        if ("enroll".equalsIgnoreCase(status))
            return changeParticipationStatus(ParticipationStatus.ENROLLED);
        else if ("reserve".equalsIgnoreCase(status))
            return changeParticipationStatus(ParticipationStatus.RESERVED);
        else if ("cancel".equals(status))
            return changeParticipationStatus(ParticipationStatus.CANCELLED);
        else
            return renderInvalid(UserErrorCode.INVALID_ENROLL_STATUS);
    }
    
    private String changeParticipationStatus(ParticipationStatus status) throws PartakeException, DAOException {
        UserEx user = ensureLogin();

        String eventId = getValidEventIdParameter();

        // If the comment does not exist, we use empty string instead.
        String comment = getParameter("comment");
        if (comment == null) { comment = ""; }
        if (comment.length() > 1024)
            return renderInvalid(UserErrorCode.INVALID_COMMENT_TOOLONG);

        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        Date deadline = event.getCalculatedDeadline();

        // もし、締め切りを過ぎている場合、変更が出来なくなる。
        if (deadline.before(new Date()))
            return renderInvalid(UserErrorCode.INVALID_ENROLL_TIMEOVER);

        // 現在の状況が登録されていない場合、
        List<EventRelationEx> relations = EventService.get().getEventRelationsEx(eventId);
        ParticipationStatus currentStatus = UserService.get().getParticipationStatus(user.getId(), event.getId());
        if (!currentStatus.isEnrolled()) {
            List<Event> requiredEvents = getRequiredEventsNotEnrolled(user, relations);
            if (requiredEvents != null && !requiredEvents.isEmpty())
                return renderInvalid(UserErrorCode.INVALID_ENROLL_REQUIRED);
        }

        EventService.get().enroll(user.getId(), event.getId(), status, comment, false, event.isReservationTimeOver());

        JSONObject obj = new JSONObject();
        // Twitter で参加をつぶやく
        tweetEnrollment(obj, user, event, status);

        return renderOK(obj);
    }
    
    private void tweetEnrollment(JSONObject obj, UserEx user, EventEx event, ParticipationStatus status) throws DAOException {
        UserPreference pref = UserService.get().getUserPreference(user.getId());
        if (pref == null || !pref.tweetsAttendanceAutomatically()) { return; }

        String left = "[PARTAKE] ";
        String right;
        switch (status) {
        case ENROLLED:
            right = " (" + event.getShortenedURL() + ") へ参加します。";
            break;
        case RESERVED:
            right = " (" + event.getShortenedURL() + ") へ参加予定です。";
            break;
        case CANCELLED:
            right = " (" + event.getShortenedURL() + ") への参加を取りやめました。";
            break;
        default:
            right = null;
        }

        if (right == null) {
            if (!obj.containsKey("warning"))
                obj.put("warning", new JSONArray());
            obj.getJSONArray("warning").add("参加予定 tweet に失敗しました。");
            return;
        }

        String message = left + Util.shorten(event.getTitle(), 140 - Util.codePointCount(left) - Util.codePointCount(right)) + right;
        MessageService.get().tweetMessage(user, message);
    }
}
