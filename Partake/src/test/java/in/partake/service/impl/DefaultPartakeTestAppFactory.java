package in.partake.service.impl;

import in.partake.service.ITestService;

public class DefaultPartakeTestAppFactory extends DefaultPartakeAppFactory {

    public ITestService createTestService() {
        return new TestService();
    };
}
