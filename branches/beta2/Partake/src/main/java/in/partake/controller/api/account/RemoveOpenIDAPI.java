package in.partake.controller.api.account;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade;
import in.partake.resource.UserErrorCode;

public class RemoveOpenIDAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    public String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();

        // check arguments
        String identifier = getParameter("identifier");
        if (identifier == null)
            return renderInvalid(UserErrorCode.MISSING_OPENID);

        // identifier が user と結び付けられているか検査して消去
        if (DeprecatedUserDAOFacade.get().removeOpenIDLinkage(user.getId(), identifier))
            return renderOK();
        else
            return renderInvalid(UserErrorCode.INVALID_OPENID);

    }

}
