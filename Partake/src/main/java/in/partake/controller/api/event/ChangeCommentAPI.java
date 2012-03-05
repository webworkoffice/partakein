package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.UserErrorCode;

import java.util.Date;
import java.util.List;

public class ChangeCommentAPI extends AbstractEventAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws PartakeException, DAOException {
        return changeParticipationStatus(null);
    }
    
    // TODO: EnrollAPI has a similar function. We should consider merge it or split
    private String changeParticipationStatus(ParticipationStatus status) throws PartakeException, DAOException {
        UserEx user = ensureLogin();

        String eventId = getValidEventIdParameter();

        // If the comment does not exist, we use empty string instead.
        String comment = getParameter("comment");
        if (comment == null) { comment = ""; }
        if (comment.length() > 1024)
            return renderInvalid(UserErrorCode.INVALID_COMMENT_TOOLONG);

        EventEx event = DeprecatedEventDAOFacade.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        Date deadline = event.getCalculatedDeadline();

        // もし、締め切りを過ぎている場合、変更が出来なくなる。
        if (deadline.before(new Date()))
            return renderInvalid(UserErrorCode.INVALID_ENROLL_TIMEOVER);

        // 現在の状況が登録されていない場合、
        List<EventRelationEx> relations = DeprecatedEventDAOFacade.get().getEventRelationsEx(eventId);
        ParticipationStatus currentStatus = DeprecatedUserDAOFacade.get().getParticipationStatus(user.getId(), event.getId());
        if (!currentStatus.isEnrolled()) {
            List<Event> requiredEvents = getRequiredEventsNotEnrolled(user, relations);
            if (requiredEvents != null && !requiredEvents.isEmpty())
                return renderInvalid(UserErrorCode.INVALID_ENROLL_REQUIRED);
        }

        // TODO: EventService should have a function to change comment.
        // We should not use 'enroll' here.
        DeprecatedEventDAOFacade.get().enroll(user.getId(), event.getId(), status, comment, true, event.isReservationTimeOver());

        return renderOK();
    }
}
