package in.partake.controller.api.account;

import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.UserPreference;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.service.UserService;
import in.partake.servlet.PartakeSession;

import java.util.List;

import net.sf.json.JSONObject;

public class AccountAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;
    // private static final Logger logger = Logger.getLogger(AccountAction.class);

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

    public String get() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null) { return renderLoginRequired(); }

        JSONObject obj = user.toSafeJSON();
        
        UserPreference pref = UserService.get().getUserPreference(user.getId());
        if (pref != null)
            obj.put("preference", pref.toSafeJSON());        

        List<String> openIds = UserService.get().getOpenIDIdentifiers(user.getId());
        if (openIds != null)
            obj.put("openId", openIds);
        
        return renderOK(obj);
    }
    
    public String setPreference() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();
        
        if (!checkSessionToken())
            return renderInvalid(UserErrorCode.INVALID_SESSION);

        Boolean profilePublic = getBooleanParameter("profilePublic");
        Boolean receivingTwitterMessage = getBooleanParameter("receivingTwitterMessage");
        Boolean tweetingAttendanceAutomatically = getBooleanParameter("tweetingAttendanceAutomatically");

        UserService.get().updateUserPreference(user.getId(), profilePublic, receivingTwitterMessage, tweetingAttendanceAutomatically);
        
        return renderOK();
    }

    public String removeOpenID() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        if (!checkSessionToken())
            return renderInvalid(UserErrorCode.INVALID_SESSION);

        // check arguments
        String identifier = getParameter("identifier");
        if (identifier == null)
            return renderInvalid(UserErrorCode.MISSING_OPENID);
        
        // identifier が user と結び付けられているか検査して消去
        if (UserService.get().removeOpenIDLinkage(user.getId(), identifier))
            return renderOK();
        else
            return renderInvalid(UserErrorCode.INVALID_OPENID);
    }
}
