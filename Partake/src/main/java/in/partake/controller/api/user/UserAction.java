package in.partake.controller.api.user;

import net.sf.json.JSONObject;
import in.partake.base.Util;
import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;
import in.partake.service.UserService;

public class UserAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;    
    // private static final Logger logger = Logger.getLogger(UserAction.class);
    
    public String get() throws DAOException {
        String userId = getParameter("userId");
        if (userId == null)
            return renderInvalid(UserErrorCode.MISSING_USER_ID);
        if (!Util.isUUID(userId))
            return renderInvalid(UserErrorCode.INVALID_USER_ID);
        
        UserEx user = UserService.get().getUserExById(userId);
        if (user == null)
            return renderNotFound();
        
        JSONObject obj = user.toSafeJSON();
        return renderOK(obj);
    }

    // 参加しているイベントを列挙
    public String getEvents() {
        String userId = getParameter("userId");
        if (userId == null)
            return renderInvalid(UserErrorCode.MISSING_USER_ID);
        if (!Util.isUUID(userId))
            return renderInvalid(UserErrorCode.INVALID_USER_ID);

        // TODO:
        throw new RuntimeException("Not implemented yet.");
    }
}
