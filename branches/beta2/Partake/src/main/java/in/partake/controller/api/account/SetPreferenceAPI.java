package in.partake.controller.api.account;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.UserService;
import in.partake.resource.UserErrorCode;

public class SetPreferenceAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    public String doExecute() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();
        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SESSION);

        Boolean profilePublic = getBooleanParameter("profilePublic");
        Boolean receivingTwitterMessage = getBooleanParameter("receivingTwitterMessage");
        Boolean tweetingAttendanceAutomatically = getBooleanParameter("tweetingAttendanceAutomatically");

        UserService.get().updateUserPreference(user.getId(), profilePublic, receivingTwitterMessage, tweetingAttendanceAutomatically);

        return renderOK();
    }

}
