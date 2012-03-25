package in.partake.controller.action.mypage;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserPreference;

import java.util.List;

public class MypageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    // private static final Logger logger = Logger.getLogger(MypageController.class);

    private UserPreference preference;
    private List<String> openIds;

    public String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();

        MypageActionTransaction transaction = new MypageActionTransaction(user.getId());
        transaction.execute();

        preference = transaction.getPreference();
        openIds = transaction.getOpenIds();

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

class MypageActionTransaction extends DBAccess<Void> {
    private String userId;
    private UserPreference preference;
    private List<String> openIds;

    public MypageActionTransaction(String userId) {
        this.userId = userId;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        preference = daos.getUserPreferenceAccess().find(con, userId);
        if (preference == null)
            preference = UserPreference.getDefaultPreference(userId);

        openIds = daos.getOpenIDLinkageAccess().findByUserId(con, userId);
        return null;
    }

    public UserPreference getPreference() {
        return preference;
    }

    public List<String> getOpenIds() {
        return openIds;
    }
}
