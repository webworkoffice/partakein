package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;


public class EventNotificationTest extends AbstractPartakeModelTest<EventNotification> {

    @Override
    protected TestDataProvider<EventNotification> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getEventNotificationProvider();
    }

    @Override
    protected EventNotification copy(EventNotification t) {
        return new EventNotification(t);
    }
}
