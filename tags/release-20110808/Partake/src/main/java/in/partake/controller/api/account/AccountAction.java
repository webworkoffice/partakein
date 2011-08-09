package in.partake.controller.api.account;

import net.sf.json.JSONObject;
import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.UserPreference;
import in.partake.service.UserService;

public class AccountAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;
    // private static final Logger logger = Logger.getLogger(AccountAction.class);

    public String get() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null) { return renderLoginRequired(); }

        // Should take the following:
        //  - user data
        //  - calendar
        //  - all events?

        throw new RuntimeException("Not implemented yet.");
    }
    
    public String getCalendar() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null) { return renderLoginRequired(); }

        throw new RuntimeException("Not implemented yet.");
    }
    
    public String getPreference() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null) { return renderLoginRequired(); }
        
        UserPreference pref = UserService.get().getUserPreference(user.getId());
        JSONObject obj = pref.toSafeJSON();
        
        return renderOK(obj);
    }
    
    public String setPreference() throws DAOException {
        throw new RuntimeException("Not implemented yet.");
    }

}
