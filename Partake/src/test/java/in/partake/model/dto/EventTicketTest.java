package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;

public final class EventTicketTest extends AbstractPartakeModelTest<EventTicket> {
    @Override
    protected EventTicket copy(EventTicket t) {
        return new EventTicket(t);
    }

    @Override
    protected TestDataProvider<EventTicket> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getEventTicketProvider();
    }
}
