package in.partake.service.impl;

import static org.mockito.Mockito.mock;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.fixture.TestDataProvider;
import in.partake.service.ITestService;
import in.partake.service.ITwitterService;
import in.partake.session.TwitterLoginInformation;
import in.partake.view.IViewInitializer;

import org.apache.log4j.Logger;
import org.mockito.Mockito;

import twitter4j.TwitterException;

public class DefaultPartakeTestAppFactory extends DefaultPartakeAppFactory {
    private static final Logger logger = Logger.getLogger(DefaultPartakeTestAppFactory.class);

    @Override
    public ITwitterService createTwitterService() {
        ITwitterService twitterService = mock(ITwitterService.class);

        try {
            TwitterLoginInformation mockInfo = mock(TwitterLoginInformation.class);

            Mockito.doReturn(mockInfo).when(twitterService).createLoginInformation(Mockito.anyString());
            Mockito.doReturn(mockInfo).when(twitterService).createLoginInformation(null);
            Mockito.doThrow(new TwitterException("MockException")).when(twitterService).createLoginInformation("http://www.example.com/throwException");

            TwitterLinkage twitterLinkage = new TwitterLinkage(TestDataProvider.DEFAULT_TWITTER_ID, TestDataProvider.DEFAULT_TWITTER_SCREENNAME,
                    "testUser 1", "accessToken", "accessTokenSecret", "http://www.example.com/", TestDataProvider.DEFAULT_USER_ID);
            Mockito.doReturn(twitterLinkage).when(twitterService).createTwitterLinkageFromLoginInformation((TwitterLoginInformation) Mockito.any(), Mockito.anyString());


            Mockito.doReturn("http://www.example.com/validAuthenticationURL").when(mockInfo).getAuthenticationURL();
        } catch (Exception e) {
            logger.error("createTwitterService mock threw an exception", e);
        }

        return twitterService;
    }

    @Override
    public ITestService createTestService() {
        return new TestService();
    }

    @Override
    public IViewInitializer createViewInitializer() {
        return null;
    }
}
