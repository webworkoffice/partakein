package in.partake.controller.action.auth;

import in.partake.app.PartakeApp;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;
import in.partake.service.ITwitterService;
import in.partake.session.TwitterLoginInformation;
import twitter4j.TwitterException;

public class LoginByTwitterAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    public String doExecute() throws DAOException {
        try {
            ITwitterService twitterService = PartakeApp.getTwitterService();
            String redirectURL = getParameter("redirectURL");
            TwitterLoginInformation info = twitterService.createLoginInformation(redirectURL);
            String url = info.getAuthenticationURL();

            getPartakeSession().setTwitterLoginInformation(info);
            return renderRedirect(url);
        } catch (TwitterException e) {
            return renderError(ServerErrorCode.TWITTER_OAUTH_ERROR, e);
        }

    }
}
