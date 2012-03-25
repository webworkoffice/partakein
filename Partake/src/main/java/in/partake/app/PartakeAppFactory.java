package in.partake.app;

import in.partake.service.IBitlyService;
import in.partake.service.IDBService;
import in.partake.service.IEventSearchService;
import in.partake.service.IOpenIDService;
import in.partake.service.ITestService;
import in.partake.service.ITwitterService;
import in.partake.view.IViewInitializer;

public interface PartakeAppFactory {
    public abstract IDBService createDBService();
    public abstract IBitlyService createBitlyService();
    public abstract IEventSearchService createEventSearchService();
    public abstract ITwitterService createTwitterService();
    public abstract ITestService createTestService();
    public abstract IOpenIDService createOpenIDService();
    public abstract IViewInitializer createViewInitializer();
}
