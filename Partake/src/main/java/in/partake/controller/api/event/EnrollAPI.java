package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.daofacade.MessageDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;

import java.util.Date;
import java.util.List;

public class EnrollAPI extends AbstractPartakeAPI {
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
        if (comment == null)
            comment = "";
        if (comment.length() > 1024)
            return renderInvalid(UserErrorCode.INVALID_COMMENT_TOOLONG);

        new EnrollTransaction(user, eventId, comment).execute();
        
        return renderOK();
    }
}

class EnrollTransaction extends Transaction<Void> {
    private UserEx user;
    private String eventId;
    private String comment;

    public EnrollTransaction(UserEx user, String eventId, String comment) {
        this.user = user;
        this.eventId = eventId;
        this.comment = comment;
    }
    
    // TODO: We should share a lot of code with ChangeEnrollmentCommentAPI.
    @Override
    protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
        EventEx event = EventDAOFacade.getEventEx(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        Date deadline = event.getCalculatedDeadline();
        // もし、締め切りを過ぎている場合、変更が出来なくなる。
        if (deadline.before(TimeUtil.getCurrentDate()))
            throw new PartakeException(UserErrorCode.INVALID_ENROLL_TIMEOVER);

        // 現在の状況が登録されていない場合、
        List<EventRelationEx> relations = EventDAOFacade.getEventRelationsEx(con, event);
        ParticipationStatus currentStatus = EnrollmentDAOFacade.getParticipationStatus(con, user.getId(), eventId); 
        if (!currentStatus.isEnrolled()) {
            List<Event> requiredEvents = EventDAOFacade.getRequiredEventsNotEnrolled(con, user, relations); 
            if (requiredEvents != null && !requiredEvents.isEmpty())
                throw new PartakeException(UserErrorCode.INVALID_ENROLL_REQUIRED);
        }

        EnrollmentDAOFacade.enrollImpl(con, user, event, currentStatus, comment, false, event.isReservationTimeOver());
        tweetEnrollment(con, user, event, currentStatus);
        return null;
    }
    
    private void tweetEnrollment(PartakeConnection con, UserEx user, EventEx event, ParticipationStatus status) throws DAOException {
        UserPreference pref = DBService.getFactory().getUserPreferenceAccess().find(con, user.getId());
        if (pref == null)
            pref = UserPreference.getDefaultPreference(user.getId());

        if (!pref.tweetsAttendanceAutomatically())
            return;

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

        if (right == null)
            return;

        String message = left + Util.shorten(event.getTitle(), 140 - Util.codePointCount(left) - Util.codePointCount(right)) + right;
        MessageDAOFacade.tweetMessageImpl(con, user, message);
    }
}
