package in.partake.controller.api.account;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.model.dto.OpenIDLinkage;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;

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
        new RemoveOpenIDLinkageTransaction(user.getId(), identifier).execute();
        return renderOK();
    }
}

class RemoveOpenIDLinkageTransaction extends Transaction<Void> {
    private String userId;
    private String identifier;

    public RemoveOpenIDLinkageTransaction(String userId, String identifier) {
        this.userId = userId;
        this.identifier = identifier;
    }

    @Override
    protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
        OpenIDLinkage linkage = DBService.getFactory().getOpenIDLinkageAccess().find(con, identifier);
        if (linkage == null || !userId.equals(linkage.getUserId()))
            throw new PartakeException(UserErrorCode.INVALID_OPENID);

        DBService.getFactory().getOpenIDLinkageAccess().remove(con, identifier);
        return null;
    }
}