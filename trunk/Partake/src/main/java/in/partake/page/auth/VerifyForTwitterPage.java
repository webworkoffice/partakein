package in.partake.page.auth;

import in.partake.application.PartakeSession;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.page.base.PartakePage;
import in.partake.resource.PartakeProperties;
import in.partake.service.UserService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class VerifyForTwitterPage extends PartakePage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(VerifyForTwitterPage.class);
    

    public VerifyForTwitterPage(PageParameters params) {
        PartakeSession session = PartakeSession.get();
        
        if (session == null) {
            logger.error("Session is null");
            renderError("Session is null");
            return;
        }
        
        Twitter twitter = (Twitter)session.get("twitter");
        RequestToken requestToken = (RequestToken)session.get("requestToken");
        
        String verifier = params.get("oauth_verifier").toString();
        String redirectURL = (String)session.get("redirectURL");

        try {
            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
            session.remove("requestToken");
            session.remove("redirectURL");

            UserEx user = UserService.get().loginUserByTwitter(twitter, accessToken);
            session.setCurrentUser(user);
            
            session.addMessage("ログインしました");
            
        } catch (TwitterException e) {
            renderError("Twitter Exception が発生しました。");
            return;
        } catch (DAOException e) {
            renderDBError();
            return;
        }
        
        if (StringUtils.isEmpty(redirectURL)) {
            renderRedirect("/");
            return;
        } else {
            // If the redirect page is the error page, we do not want to show it. Showing the top page is better.
            String errorPageURL = PartakeProperties.get().getTopPath() + "/error";
            if (errorPageURL.equals(redirectURL)) {
                renderRedirect("/");
                return;
            } else {
                renderRedirect(redirectURL);
                return;
            }
        }

    }
}
