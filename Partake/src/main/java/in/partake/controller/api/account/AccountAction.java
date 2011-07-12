package in.partake.controller.api.account;

import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;

public class AccountAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;
    // private static final Logger logger = Logger.getLogger(AccountAction.class);

    public String get() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null) { return renderLoginRequired(); }

        // Should take the following:
        //  - user data
        //  - all events?

        throw new RuntimeException("Not implemented yet.");
    }
}
