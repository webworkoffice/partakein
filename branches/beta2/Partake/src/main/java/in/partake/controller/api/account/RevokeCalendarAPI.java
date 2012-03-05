package in.partake.controller.api.account;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.CalendarLinkage;
import in.partake.resource.Constants;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;
import net.sf.json.JSONObject;

public class RevokeCalendarAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws Exception {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SESSION);

        String newCalendarId = new Transaction<UserEx, String>() {
            @Override
            protected String doTransaction(PartakeConnection con, UserEx user) throws Exception {
                return RevokeCalendarAPI.this.doTransaction(con, user);
            }
        }.transaction(user);
        
        JSONObject obj = new JSONObject();
        obj.put("calendarId", newCalendarId);
        return renderOK(obj);
    }
    
    protected String doTransaction(PartakeConnection con, UserEx user) throws Exception {
        PartakeDAOFactory factory = DBService.getFactory();
                
        String calendarId = user.getCalendarId();
        if (calendarId != null)
            factory.getCalendarAccess().remove(con, calendarId);

        // 新しくカレンダー id を作成して保存
        calendarId = factory.getCalendarAccess().getFreshId(con);
        CalendarLinkage embryo = new CalendarLinkage(calendarId, user.getId());
        factory.getCalendarAccess().put(con, embryo);

        // TODO: Unfortunately, the [user] must be updated to reflect this calendar revocation.
        // For convenient way, we retrieve user again, and set it to the session.   
        user = UserDAOFacade.getUserEx(con, user.getId());
        session.put(Constants.ATTR_USER, user);

        return calendarId;
    }
}
