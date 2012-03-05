package in.partake.controller.api.user;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.UserService;
import net.sf.json.JSONObject;

public class GetAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        String userId = getValidUserIdParameter();

        UserEx user = UserService.get().getUserExById(userId);
        if (user == null)
            return renderNotFound();
        
        JSONObject obj = user.toSafeJSON();
        return renderOK(obj);
    }
}
