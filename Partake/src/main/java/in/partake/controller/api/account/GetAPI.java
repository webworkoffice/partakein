package in.partake.controller.api.account;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade;
import in.partake.model.dto.UserPreference;

import java.util.List;

import net.sf.json.JSONObject;

public class GetAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    public String doExecute() throws DAOException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();

        JSONObject obj = user.toSafeJSON();

        UserPreference pref = DeprecatedUserDAOFacade.get().getUserPreference(user.getId());
        if (pref != null)
            obj.put("preference", pref.toSafeJSON());

        List<String> openIds = DeprecatedUserDAOFacade.get().getOpenIDIdentifiers(user.getId());
        if (openIds != null)
            obj.put("openId", openIds);

        return renderOK(obj);
    }
}
