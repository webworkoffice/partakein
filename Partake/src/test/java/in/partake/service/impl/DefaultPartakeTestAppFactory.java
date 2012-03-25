package in.partake.service.impl;

import in.partake.service.ITestService;
import in.partake.view.IViewInitializer;

public class DefaultPartakeTestAppFactory extends DefaultPartakeAppFactory {

    @Override
    public ITestService createTestService() {
        return new TestService();
    };

    @Override
    public IViewInitializer createViewInitializer() {
        return null;
    }
}
