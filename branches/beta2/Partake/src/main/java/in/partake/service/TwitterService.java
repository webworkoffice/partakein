package in.partake.service;

import in.partake.model.dto.TwitterLinkage;
import in.partake.resource.PartakeProperties;
import in.partake.session.TwitterLoginInformation;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterService {

    public static TwitterLoginInformation createLoginInformation(String redirectURL) throws TwitterException {
        Twitter twitter = new TwitterFactory().getInstance();
        String callbackURL = PartakeProperties.get().getTopPath() + "/auth/verifyForTwitter";
        RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL);

        return new TwitterLoginInformation(twitter, requestToken, redirectURL);
    }
    
    public static TwitterLinkage createTwitterLinkageFromLoginInformation(TwitterLoginInformation information, String verifier) throws TwitterException {
        Twitter twitter = information.getTwitter();
        RequestToken requestToken = information.getRequestToken();         
        AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

        twitter4j.User twitterUser = twitter.showUser(twitter.getId());
        return new TwitterLinkage(
                twitter.getId(), twitter.getScreenName(), twitterUser.getName(), accessToken.getToken(), accessToken.getTokenSecret(),
                twitter.showUser(twitter.getId()).getProfileImageURL().toString(), null
        );
    }
}
