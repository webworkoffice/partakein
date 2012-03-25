package in.partake.controller.api.account;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.CalendarLinkage;
import in.partake.resource.Constants;

import java.util.Map;

import net.sf.json.JSONObject;

public class RevokeCalendarAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();

        String newCalendarId = new RevokeCalendarAPITransaction(user, session).execute();

        JSONObject obj = new JSONObject();
        obj.put("calendarId", newCalendarId);
        return renderOK(obj);
    }
}

class RevokeCalendarAPITransaction extends Transaction<String> {
    private UserEx user;
    private Map<String, Object> session; // TODO: This is bad.

    public RevokeCalendarAPITransaction(UserEx user, Map<String, Object> session) {
        this.user = user;
        this.session = session;
    }

    protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        String calendarId = user.getCalendarId();
        if (calendarId != null)
            daos.getCalendarAccess().remove(con, calendarId);

        // 新しくカレンダー id を作成して保存
        calendarId = daos.getCalendarAccess().getFreshId(con);
        CalendarLinkage embryo = new CalendarLinkage(calendarId, user.getId());
        daos.getCalendarAccess().put(con, embryo);

        // TODO: Unfortunately, the [user] must be updated to reflect this calendar revocation.
        // For convenient way, we retrieve user again, and set it to the session.
        user = UserDAOFacade.getUserEx(con, daos, user.getId());
        session.put(Constants.ATTR_USER, user);

        return calendarId;
    }

}
