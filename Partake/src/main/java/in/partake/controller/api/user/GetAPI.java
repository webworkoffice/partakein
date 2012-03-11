package in.partake.controller.api.user;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.resource.UserErrorCode;

public class GetAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        String userId = getValidUserIdParameter();

        UserEx user = new GetAPITransaction(userId).execute();
        if (user == null)
            return renderInvalid(UserErrorCode.INVALID_USER_ID);

        return renderOK(user.toSafeJSON());
    }
}

class GetAPITransaction extends Transaction<UserEx> {
    private String userId;

    public GetAPITransaction(String userId) {
        this.userId = userId;
    }

    @Override
    protected UserEx doExecute(PartakeConnection con) throws DAOException, PartakeException {
        return UserDAOFacade.getUserEx(con, userId); 
    }
}
