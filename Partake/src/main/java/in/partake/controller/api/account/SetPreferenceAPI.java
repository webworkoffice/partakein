package in.partake.controller.api.account;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.base.Transaction;
import in.partake.model.dto.UserPreference;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;

public class SetPreferenceAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    public String doExecute() throws DAOException, PartakeException {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();
        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SESSION);

        new SetPreferenceAPITransaction(
                user,
                getBooleanParameter("profilePublic"),
                getBooleanParameter("receivingTwitterMessage"),
                getBooleanParameter("tweetingAttendanceAutomatically")
        ).execute();

        return renderOK();
    }
}

class SetPreferenceAPITransaction extends Transaction<Void> {
    UserEx user;
    Boolean profilePublic;
    Boolean receivingTwitterMessage;
    Boolean tweetingAttendanceAutomatically;
    
    public SetPreferenceAPITransaction(UserEx user, Boolean profilePublic, Boolean receivingTwitterMessage, Boolean tweetingAttendanceAutomatically) {  
        this.user = user;
        this.profilePublic = profilePublic;
        this.receivingTwitterMessage = receivingTwitterMessage;
        this.tweetingAttendanceAutomatically = tweetingAttendanceAutomatically;
    }
    
    /**
     * Updates UserPreference. Null arguments won't be updated.
     */
    public Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
        PartakeDAOFactory factory = DBService.getFactory();
        
        final UserPreference pref = factory.getUserPreferenceAccess().find(con, user.getId());
        UserPreference newPref = new UserPreference(pref != null ? pref : UserPreference.getDefaultPreference(user.getId()));
        
        if (profilePublic != null)
            newPref.setProfilePublic(profilePublic);
        if (receivingTwitterMessage != null)
            newPref.setReceivingTwitterMessage(receivingTwitterMessage);
        if (tweetingAttendanceAutomatically != null)
            newPref.setTweetingAttendanceAutomatically(tweetingAttendanceAutomatically);
        
        factory.getUserPreferenceAccess().put(con, newPref);
        return null;
    }

}


