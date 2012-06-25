package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;


public class EventTicketNotificationTest extends AbstractPartakeModelTest<EventTicketNotification> {

    @Override
    protected TestDataProvider<EventTicketNotification> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getEventTicketNotificationProvider();
    }

    @Override
    protected EventTicketNotification copy(EventTicketNotification t) {
        return new EventTicketNotification(t);
    }
}
