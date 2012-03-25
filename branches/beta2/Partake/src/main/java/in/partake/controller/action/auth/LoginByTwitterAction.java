package in.partake.controller.action.auth;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.service.ITwitterService;
import in.partake.service.PartakeService;
import in.partake.session.TwitterLoginInformation;
import twitter4j.TwitterException;

public class LoginByTwitterAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    public String doExecute() throws DAOException {
       if (session == null)
           return renderInvalid(UserErrorCode.MISSING_SESSION);
        
        try {
            ITwitterService twitterService = PartakeService.get().getTwitterService();
            TwitterLoginInformation info = twitterService.createLoginInformation(getParameter("redirectURL"));
            String url = info.getRequestToken().getAuthenticationURL();

            getPartakeSession().setTwitterLoginInformation(info);
            return renderRedirect(url);
        } catch (TwitterException e) {
            return renderError(ServerErrorCode.TWITTER_OAUTH_ERROR, e);
        }

    }
}
