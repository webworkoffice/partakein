package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;

public final class EnrollmentTest extends AbstractPartakeModelTest<Enrollment> {
    @Override
    protected Enrollment copy(Enrollment t) {
        return new Enrollment(t);
    }

    @Override
    protected TestDataProvider<Enrollment> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getEnrollmentProvider();
    }
}
