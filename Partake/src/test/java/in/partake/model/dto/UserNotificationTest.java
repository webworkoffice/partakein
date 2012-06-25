package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;


public class UserNotificationTest extends AbstractPartakeModelTest<UserNotification> {

    @Override
    protected TestDataProvider<UserNotification> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getUserNotificationProvider();
    }

    @Override
    protected UserNotification copy(UserNotification t) {
        return new UserNotification(t);
    }
}
