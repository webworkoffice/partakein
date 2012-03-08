package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.UserPermission;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;

public class RemoveAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();        
        String eventId = getValidEventIdParameter();

        EventEx event = DeprecatedEventDAOFacade.get().getEventExById(eventId);
        if (event == null)
            return renderNotFound();
        
        if (!event.hasPermission(user, UserPermission.EVENT_REMOVE))
            return renderForbidden();

        DeprecatedEventDAOFacade.get().remove(eventId);
        return renderOK();
    }
}
