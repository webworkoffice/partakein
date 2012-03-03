package in.partake.controller.api.account;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.CalendarService;
import in.partake.model.daofacade.deprecated.UserService;
import in.partake.resource.Constants;
import in.partake.resource.UserErrorCode;
import net.sf.json.JSONObject;

public class RemoveCalendarAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        if (!checkCSRFToken())
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
