package in.partake.page.auth;

import in.partake.application.PartakeSession;
import in.partake.page.base.PartakePage;
import in.partake.resource.PartakeProperties;

import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.RequestToken;

public class LoginByTwitterPage extends PartakePage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(LoginByTwitterPage.class);
    

    public LoginByTwitterPage(PageParameters params) {
        String redirectURL = params.get("redirectURL").toOptionalString();
        
        PartakeSession session = PartakeSession.get();
        if (session == null) {
            renderError("Session could not be retrieved.");
        }
        
        Twitter twitter = new TwitterFactory().getInstance();
        session.put("twitter", twitter);
        try {
            String callbackURL = PartakeProperties.get().getTopPath() + "/auth/verifyForTwitter";            
            RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL.toString());

            session.put("requestToken", requestToken);           
            session.put("redirectURL", redirectURL);
            
            renderRedirect(requestToken.getAuthenticationURL());
        } catch (TwitterException e) {
            logger.warn("twitter exception", e);
            renderError("TwitterException");
        }

    }
}
