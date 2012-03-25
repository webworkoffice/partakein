package in.partake.service.impl;

import static org.mockito.Mockito.mock;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.fixture.TestDataProvider;
import in.partake.service.ITestService;
import in.partake.service.ITwitterService;
import in.partake.service.IViewInitializer;
import in.partake.session.TwitterLoginInformation;

import org.mockito.Mockito;

import twitter4j.TwitterException;

public class DefaultPartakeTestAppFactory extends DefaultPartakeAppFactory {
    @Override
    public ITwitterService createTwitterService() throws Exception {
        ITwitterService twitterService = mock(ITwitterService.class);

        TwitterLoginInformation mockInfo = mock(TwitterLoginInformation.class);
        Mockito.doReturn(mockInfo).when(twitterService).createLoginInformation(Mockito.anyString());
        Mockito.doReturn(mockInfo).when(twitterService).createLoginInformation(null);
        Mockito.doThrow(new TwitterException("MockException")).when(twitterService).createLoginInformation("http://www.example.com/throwException");

        TwitterLinkage twitterLinkage = new TwitterLinkage(TestDataProvider.DEFAULT_TWITTER_ID, TestDataProvider.DEFAULT_TWITTER_SCREENNAME,
                "testUser 1", "accessToken", "accessTokenSecret", "http://www.example.com/", TestDataProvider.DEFAULT_USER_ID);
        Mockito.doReturn(twitterLinkage).when(twitterService).createTwitterLinkageFromLoginInformation((TwitterLoginInformation) Mockito.any(), Mockito.anyString());


        Mockito.doReturn("http://www.example.com/validAuthenticationURL").when(mockInfo).getAuthenticationURL();

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
