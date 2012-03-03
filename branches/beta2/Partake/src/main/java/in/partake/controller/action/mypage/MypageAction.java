package in.partake.controller.action.mypage;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.UserService;
import in.partake.model.dto.UserPreference;

import java.util.List;

public class MypageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    // private static final Logger logger = Logger.getLogger(MypageController.class);

    private UserPreference preference;
    private List<String> openIds;
    
    public String doExecute() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        preference = UserService.get().getUserPreference(user.getId());
        openIds = UserService.get().getOpenIDIdentifiers(user.getId());
        
        return render("mypage/show.jsp");
    }
    
    // ----------------------------------------------------------------------
    
    public UserPreference getPreference() {
        return preference;
    }

    public List<String> getOpenIds() {
        return openIds;
    }
}
