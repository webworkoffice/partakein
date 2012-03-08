package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.UserPermission;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.resource.UserErrorCode;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public class GetAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        String eventId = getValidEventIdParameter();
        
        EventEx event = DeprecatedEventDAOFacade.get().getEventExById(eventId);
        if (event == null) { return renderInvalid(UserErrorCode.INVALID_EVENT_ID); } 
            
        if (event.isPrivate()) {
            // TODO: EventsController とコードが同じなので共通化するべき　
      
            // owner および manager は見ることが出来る。
            // TOOD: Use PartakeSession instead of session.
            String passcode = (String) session.get("event:" + eventId);
            if (passcode == null) { passcode = getParameter("passcode"); }
      
            UserEx loginUser = getLoginUser();
            if (loginUser != null && event.hasPermission(loginUser, UserPermission.EVENT_PRIVATE_EVENT)) {
                // OK. You have the right to show this event.
            } else if (StringUtils.equals(event.getPasscode(), passcode)) {
                // OK. The same passcode. 
            } else {
                // public でなければ、passcode を入れなければ見ることが出来ない
                return renderForbidden();
            }
        }

        JSONObject obj = new JSONObject();
        obj.put("event", event.toSafeJSON());
        return renderOK(obj);
    }
}
