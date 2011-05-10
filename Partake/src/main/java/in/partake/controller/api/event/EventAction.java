package in.partake.controller.api.event;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.UserPermission;
import in.partake.service.EventService;

public class EventAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;

    public String get() throws DAOException {
        String eventId = getParameter("eventId");
        if (StringUtils.isBlank(eventId)) { return renderInvalid("invalid eventId was specified."); }
        
        EventEx event = EventService.get().getEventExById(eventId);
        if (event == null) { return renderInvalid("invalid eventId was specified."); }
            
        if (event.isPrivate()) {
            // TODO: EventsController とコードが同じなので共通化するべき　
      
            // owner および manager は見ることが出来る。
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
        obj.put("event", event.toJSON());
        return renderOK(obj);
    }
}
