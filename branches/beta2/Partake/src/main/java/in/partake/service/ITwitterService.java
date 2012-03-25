package in.partake.service;

import in.partake.model.dto.TwitterLinkage;
import in.partake.session.TwitterLoginInformation;
import twitter4j.TwitterException;

public interface ITwitterService {
    public TwitterLoginInformation createLoginInformation(String redirectURL) throws TwitterException;
    public TwitterLinkage createTwitterLinkageFromLoginInformation(TwitterLoginInformation information, String verifier) throws TwitterException;
}
