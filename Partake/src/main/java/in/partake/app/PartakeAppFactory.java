package in.partake.app;

import in.partake.service.IBitlyService;
import in.partake.service.IDBService;
import in.partake.service.IEventSearchService;
import in.partake.service.IOpenIDService;
import in.partake.service.IDaemonInitializer;
import in.partake.service.ITestService;
import in.partake.service.ITwitterService;
import in.partake.service.IViewInitializer;

/**
 * PartakeAppFactory is a collection of factory methods to make PARTAKE independent from external resources.
 *
 *
 *
 * @author shinyak
 *
 */
public interface PartakeAppFactory {
    public abstract IDBService createDBService() throws Exception;
    public abstract IBitlyService createBitlyService() throws Exception;
    public abstract IEventSearchService createEventSearchService() throws Exception;
    public abstract ITwitterService createTwitterService() throws Exception;
    public abstract ITestService createTestService() throws Exception;
    public abstract IOpenIDService createOpenIDService() throws Exception;
    public abstract IViewInitializer createViewInitializer() throws Exception;
    public abstract IDaemonInitializer createDaemonInitializer() throws Exception;
}
