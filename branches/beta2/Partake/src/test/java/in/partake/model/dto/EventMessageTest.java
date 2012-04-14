package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;


public class EventMessageTest extends AbstractPartakeModelTest<EventMessage> {

    @Override
    protected TestDataProvider<EventMessage> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getEventMessageProvider();
    }

    @Override
    protected EventMessage copy(EventMessage t) {
        return new EventMessage(t);
    }
}
