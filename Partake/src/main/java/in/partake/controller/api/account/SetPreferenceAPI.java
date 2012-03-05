package in.partake.controller.api.account;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.base.Transaction;
import in.partake.model.dto.UserPreference;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;

class SetPreferenceParams {
    UserEx user;
    Boolean profilePublic;
    Boolean receivingTwitterMessage;
    Boolean tweetingAttendanceAutomatically;
    
    public SetPreferenceParams(UserEx user, 
            Boolean profilePublic, 
            Boolean receivingTwitterMessage,
            Boolean tweetingAttendanceAutomatically) {
        this.user = user;
        this.profilePublic = profilePublic;
        this.receivingTwitterMessage = receivingTwitterMessage;
        this.tweetingAttendanceAutomatically = tweetingAttendanceAutomatically;
    }
}

public class SetPreferenceAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    public String doExecute() throws Exception {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();
        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SESSION);

        SetPreferenceParams param = new SetPreferenceParams(
                user,
                getBooleanParameter("profilePublic"),
                getBooleanParameter("receivingTwitterMessage"),
                getBooleanParameter("tweetingAttendanceAutomatically"));
        
        new Transaction<SetPreferenceParams, Void>() {
            protected Void doTransaction(PartakeConnection con, SetPreferenceParams param) throws Exception {
                SetPreferenceAPI.this.doTransaction(con, param);
                return null;
            }
        }.transaction(param);
        
        return renderOK();
    }

    /**
     * Updates UserPreference. Null arguments won't be updated.
     */
    public void doTransaction(PartakeConnection con, SetPreferenceParams param) throws Exception {
        PartakeDAOFactory factory = DBService.getFactory();
        
        final UserPreference pref = factory.getUserPreferenceAccess().find(con, param.user.getId());
        UserPreference newPref = new UserPreference(pref != null ? pref : UserPreference.getDefaultPreference(param.user.getId()));
        
        if (param.profilePublic != null)
            newPref.setProfilePublic(param.profilePublic);
        if (param.receivingTwitterMessage != null)
            newPref.setReceivingTwitterMessage(param.receivingTwitterMessage);
        if (param.tweetingAttendanceAutomatically != null)
            newPref.setTweetingAttendanceAutomatically(param.tweetingAttendanceAutomatically);
        
        factory.getUserPreferenceAccess().put(con, newPref);
    }
}
