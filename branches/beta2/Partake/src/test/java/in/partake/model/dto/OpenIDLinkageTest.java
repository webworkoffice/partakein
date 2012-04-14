package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;

public class OpenIDLinkageTest extends AbstractPartakeModelTest<OpenIDLinkage> {
    @Override
    protected OpenIDLinkage copy(OpenIDLinkage t) {
        return new OpenIDLinkage(t);
    }

    @Override
    protected TestDataProvider<OpenIDLinkage> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getOpenIDLinkageProvider();
    }
}
