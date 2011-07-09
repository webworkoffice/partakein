package in.partake.controller.api.account;

import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.servlet.PartakeSession;
import net.sf.json.JSONObject;

public class SessionAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;    
    // private static final Logger logger = Logger.getLogger(SessionAction.class);
    
    public String getSessionToken() throws DAOException {
        PartakeSession session = getPartakeSession();
        if (session == null) {
            return renderInvalid(UserErrorCode.MISSING_SESSION);
        }
        if (session.getCSRFPrevention() == null) {
            return renderError(ServerErrorCode.NO_CSRF_PREVENTION);
        }
        if (session.getCSRFPrevention().getSessionToken() == null) {
            return renderError(ServerErrorCode.NO_CREATED_SESSION_TOKEN);
        }
        
        JSONObject obj = new JSONObject();
        obj.put("token", session.getCSRFPrevention().getSessionToken());
        
        return renderOK(obj);
    }
}
