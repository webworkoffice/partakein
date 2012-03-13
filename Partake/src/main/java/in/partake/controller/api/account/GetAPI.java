package in.partake.controller.api.account;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.model.dto.UserPreference;
import in.partake.service.DBService;

import java.util.List;

import net.sf.json.JSONObject;

public class GetAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    public String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();

        GetAPITransaction transaction = new GetAPITransaction(user.getId());
        transaction.execute();
        
        JSONObject obj = user.toSafeJSON();
        obj.put("preference", transaction.getPreference().toSafeJSON());
        obj.put("openId", transaction.getOpenIds());
        return renderOK(obj);
    }
}

class GetAPITransaction extends Transaction<Void> {
    private String userId;
    private UserPreference preference;
    private List<String> openIds; 
    
    public GetAPITransaction(String userId) {
        this.userId = userId;
    }
    
    @Override
    protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
        preference = DBService.getFactory().getUserPreferenceAccess().find(con, userId);
        if (preference == null)
            preference = UserPreference.getDefaultPreference(userId);

        openIds = DBService.getFactory().getOpenIDLinkageAccess().findByUserId(con, userId);
        return null;
    }
    
    public UserPreference getPreference() {
        return preference;
    }

    public List<String> getOpenIds() {
        return openIds;
    }
}
