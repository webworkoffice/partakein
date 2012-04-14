package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;


public class TwitterMessageTest extends AbstractPartakeModelTest<TwitterMessage> {
    @Override
    protected TwitterMessage copy(TwitterMessage t) {
        return new TwitterMessage(t);
    }

    @Override
    protected TestDataProvider<TwitterMessage> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getTwitterMessageProvider();
    }
}
