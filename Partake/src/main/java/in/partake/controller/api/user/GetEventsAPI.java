package in.partake.controller.api.user;

import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;

public class GetEventsAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException {
        String userId = getParameter("userId");
        if (userId == null)
            return renderInvalid(UserErrorCode.MISSING_USER_ID);
        if (!Util.isUUID(userId))
            return renderInvalid(UserErrorCode.INVALID_USER_ID);

        // TODO:
        throw new RuntimeException("Not implemented yet.");
    }
}
