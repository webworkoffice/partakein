package in.partake.controller.api.event;

import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.UserErrorCode;

public class EnrollAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();
        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SECURITY_CSRF);

        String eventId = getParameter("eventId");
        if (eventId == null) 
            return renderInvalid(UserErrorCode.MISSING_EVENT_ID);
        if (!Util.isUUID(eventId))
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        // If the comment does not exist, we use empty string instead.
        String comment = getParameter("comment");
        if (comment == null) { comment = ""; }
        if (comment.length() > 1024)
            return renderInvalid(UserErrorCode.INVALID_COMMENT_TOOLONG);

        ParticipationStatus status = ParticipationStatus.safeValueOf(getParameter("status"));
        if (status == null || status == ParticipationStatus.NOT_ENROLLED)
            return renderInvalid(UserErrorCode.INVALID_ENROLL_STATUS);
        
        throw new RuntimeException("Not implemented yet.");
//        try {
//            EventService.get().enrollForAPI(user, eventId, status, comment);
//            return renderOK();
//            
//        } catch (PartakeException e) {
//            return renderException(e);
//        }
    }
}
