package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.UserErrorCode;

import java.util.Date;
import java.util.List;

public class ChangeEnrollmentCommentAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws PartakeException, DAOException {
        return changeParticipationStatus(null);
    }

    // TODO: EnrollAPI has a similar function. We should consider merge it or split
    private String changeParticipationStatus(ParticipationStatus status) throws PartakeException, DAOException {
        UserEx user = ensureLogin();
        String eventId = getValidEventIdParameter();
        ensureValidSessionToken();

        // If the comment does not exist, we use empty string instead.
        String comment = getParameter("comment");
        if (comment == null)
            comment = "";
        if (comment.length() > 1024)
            return renderInvalid(UserErrorCode.INVALID_COMMENT_TOOLONG);

        new ChangeEnrollmentCommentTransaction(user, eventId, comment).execute();
        return renderOK();
    }
}

class ChangeEnrollmentCommentTransaction extends Transaction<Void> {
    private UserEx user;
    private String eventId;
    private String comment;

    public ChangeEnrollmentCommentTransaction(UserEx user, String eventId, String comment) {
        this.user = user;
        this.eventId = eventId;
        this.comment = comment;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        EventEx event = EventDAOFacade.getEventEx(con, daos, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);

        Date deadline = event.getCalculatedDeadline();

        // もし、締め切りを過ぎている場合、変更が出来なくなる。
        if (deadline.before(TimeUtil.getCurrentDate()))
            throw new PartakeException(UserErrorCode.INVALID_ENROLL_TIMEOVER);

        // 現在の状況が登録されていない場合、
        List<EventRelationEx> relations = EventDAOFacade.getEventRelationsEx(con, daos, event);
        ParticipationStatus currentStatus = EnrollmentDAOFacade.getParticipationStatus(con, daos, user.getId(), eventId);
        if (!currentStatus.isEnrolled()) {
            List<Event> requiredEvents = EventDAOFacade.getRequiredEventsNotEnrolled(con, daos, user, relations);
            if (requiredEvents != null && !requiredEvents.isEmpty())
                throw new PartakeException(UserErrorCode.INVALID_ENROLL_REQUIRED);
        }

        // TODO: EventService should have a function to change comment.
        // We should not use 'enroll' here.
        EnrollmentDAOFacade.enrollImpl(con, daos, user, event, currentStatus, comment, true, event.isReservationTimeOver());
        return null;
    }
}

