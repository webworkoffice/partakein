package in.partake.session;

import twitter4j.Twitter;
import twitter4j.auth.RequestToken;

public class TwitterLoginInformation {
    private Twitter twitter;
    private RequestToken requestToken;
    private String redirectURL;

    public TwitterLoginInformation(Twitter twitter, RequestToken requestToken, String redirectURL) {
        this.twitter = twitter;
        this.requestToken = requestToken;
        this.redirectURL = redirectURL;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public RequestToken getRequestToken() {
        return requestToken;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public String getAuthenticationURL() {
        return getRequestToken().getAuthenticationURL();
    }
}
