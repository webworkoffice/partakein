package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.UserPermission;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.resource.UserErrorCode;

public class RemoveAttendantAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();        
        String eventId = getValidEventIdParameter();
        String userId = getValidUserIdParameter();
            
        EventEx event = DeprecatedEventDAOFacade.get().getEventExById(eventId);
        if (event == null)
            return renderInvalid(UserErrorCode.INVALID_EVENT_ID);

        // Only owner can retrieve the participants list.
        if (!event.hasPermission(user, UserPermission.EVENT_EDIT_PARTICIPANTS))
            return renderInvalid(UserErrorCode.FORBIDDEN_EVENT_ATTENDANT_EDIT);
            
        if (DeprecatedEventDAOFacade.get().removeEnrollment(eventId, userId))
            return renderOK();
        
        return renderInvalid(UserErrorCode.INVALID_ATTENDANT_EDIT);
    }
}
