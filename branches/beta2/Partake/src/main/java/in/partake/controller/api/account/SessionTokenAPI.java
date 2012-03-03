package in.partake.controller.api.account;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.session.PartakeSession;
import net.sf.json.JSONObject;

public class SessionTokenAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    public String doExecute() throws DAOException {
        PartakeSession session = getPartakeSession();
        if (session == null)
            return renderInvalid(UserErrorCode.MISSING_SESSION);

        if (session.getCSRFPrevention() == null)
            return renderError(ServerErrorCode.NO_CSRF_PREVENTION);

        if (session.getCSRFPrevention().getSessionToken() == null)
            return renderError(ServerErrorCode.NO_CREATED_SESSION_TOKEN);

        JSONObject obj = new JSONObject();
        obj.put("token", session.getCSRFPrevention().getSessionToken());

        return renderOK(obj);
    }
}
