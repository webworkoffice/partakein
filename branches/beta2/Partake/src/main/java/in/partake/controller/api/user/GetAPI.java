package in.partake.controller.api.user;

import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.UserService;
import in.partake.resource.UserErrorCode;
import net.sf.json.JSONObject;

public class GetAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException {
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
}
