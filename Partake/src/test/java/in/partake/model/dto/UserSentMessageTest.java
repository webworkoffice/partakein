package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;


public class UserSentMessageTest extends AbstractPartakeModelTest<UserSentMessage> {
    @Override
    protected UserSentMessage copy(UserSentMessage t) {
        return new UserSentMessage(t);
    }

    @Override
    protected TestDataProvider<UserSentMessage> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getUserSentMessageProvider();
    }
}
