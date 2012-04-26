package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;

public class UserOpenIDLinkTest extends AbstractPartakeModelTest<UserOpenIDLink> {
    @Override
    protected UserOpenIDLink copy(UserOpenIDLink t) {
        return new UserOpenIDLink(t);
    }

    @Override
    protected TestDataProvider<UserOpenIDLink> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getOpenIDLinkageProvider();
    }
}
