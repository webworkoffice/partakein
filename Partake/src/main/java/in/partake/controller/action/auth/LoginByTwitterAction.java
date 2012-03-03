package in.partake.controller.action.auth;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.resource.PartakeProperties;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

public class LoginByTwitterAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    public String doExecute() throws DAOException {
       if (session == null)
           return renderInvalid(UserErrorCode.MISSING_SESSION);
        
        Twitter twitter = new TwitterFactory().getInstance();
        
        session.put("twitter", twitter);
        try {
            // TODO: request.getContextPath() + "auth/twitter" とかやると twitter に飛ばされてしまう罠。
            // うーん、なんかきれいにならんもんかねえ。
            // servletpath とか設定すればいいんかな？
            
            String callbackURL = PartakeProperties.get().getTopPath() + "/auth/verifyForTwitter";
            RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL.toString());

            session.put("requestToken", requestToken);
            session.put("redirectURL", getParameter("redirectURL"));
            
            return renderRedirect(requestToken.getAuthenticationURL());
        } catch (TwitterException e) {
            return renderError(ServerErrorCode.TWITTER_OAUTH_ERROR, e);
        }

    }
}
