package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;

public class TwitterLinkageTest extends AbstractPartakeModelTest<TwitterLinkage> {
    @Override
    protected TwitterLinkage copy(TwitterLinkage t) {
        return new TwitterLinkage(t);
    }

    @Override
    protected TestDataProvider<TwitterLinkage> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getTwitterLinkageProvider();
    }
}
