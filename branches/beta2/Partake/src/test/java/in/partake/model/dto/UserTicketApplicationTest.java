package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;

public final class UserTicketApplicationTest extends AbstractPartakeModelTest<UserTicketApplication> {
    @Override
    protected UserTicketApplication copy(UserTicketApplication t) {
        return new UserTicketApplication(t);
    }

    @Override
    protected TestDataProvider<UserTicketApplication> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getEnrollmentProvider();
    }
}
