package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.UserPermission;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.resource.UserErrorCode;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public class GetAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        String eventId = getValidEventIdParameter();
        UserEx user = getLoginUser();
        String passcode = getParameter("passcode");
        if (passcode == null)
            passcode = (String) session.get("event:" + eventId);
        
        EventEx event = new GetTransaction(user, eventId, passcode).execute();
        
        JSONObject obj = new JSONObject();
        obj.put("event", event.toSafeJSON());
        return renderOK(obj);
    }
}

class GetTransaction extends Transaction<EventEx> {
    private String eventId;
    private UserEx user;
    private String passcode;
    
    public GetTransaction(UserEx user, String eventId, String passcode) {
        this.user = user;
        this.eventId = eventId;
        this.passcode = passcode;
    }
    
    @Override
    protected EventEx doExecute(PartakeConnection con) throws DAOException, PartakeException {
        EventEx event = EventDAOFacade.getEventEx(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);
            
        if (event.isPrivate()) {
            // owner および manager は見ることが出来る。
            if (user != null && event.hasPermission(user, UserPermission.EVENT_PRIVATE_EVENT)) {
                // OK. You have the right to show this event.
            } else if (StringUtils.equals(event.getPasscode(), passcode)) {
                // OK. The same passcode. 
            } else {
                // public でなければ、passcode を入れなければ見ることが出来ない
                throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_SHOW);
            }
        }
        
        return event;
    }
}
