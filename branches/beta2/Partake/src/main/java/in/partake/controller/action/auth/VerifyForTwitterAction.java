package in.partake.controller.action.auth;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.UserService;
import in.partake.resource.Constants;
import in.partake.resource.PartakeProperties;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import org.apache.commons.lang.StringUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class VerifyForTwitterAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    public String doExecute() throws DAOException {
        if (session == null)
            return renderInvalid(UserErrorCode.MISSING_SESSION);

        Twitter twitter = (Twitter) session.get("twitter");
        if (twitter == null)
            return renderInvalid(UserErrorCode.UNEXPECTED_REQUEST);

        RequestToken requestToken = (RequestToken) session.get("requestToken");

        String verifier = getParameter("oauth_verifier");
        String redirectURL = (String)session.get("redirectURL");

        try {
            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
            session.remove("requestToken");
            session.remove("redirectURL");

            UserEx user = UserService.get().loginUserByTwitter(twitter, accessToken);
            session.put(Constants.ATTR_USER, user);

            addActionMessage("ログインしました");
        } catch (TwitterException e) {
            return renderError(ServerErrorCode.TWITTER_OAUTH_ERROR);
        }

        if (StringUtils.isEmpty(redirectURL))
            return renderRedirect("/");

        // If the redirect page is the error page, we do not want to show it. Showing the top page is better.
        String errorPageURL = PartakeProperties.get().getTopPath() + "/error";
        if (errorPageURL.equals(redirectURL))
            return renderRedirect("/");
        
        return renderRedirect(redirectURL);
    }
}
