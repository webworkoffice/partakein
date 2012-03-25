package in.partake.service.impl;

import in.partake.app.PartakeAppFactory;
import in.partake.service.IBitlyService;
import in.partake.service.IDBService;
import in.partake.service.IEventSearchService;
import in.partake.service.IOpenIDService;
import in.partake.service.ITestService;
import in.partake.service.ITwitterService;
import in.partake.view.IViewInitializer;
import in.partake.view.impl.ViewInitializerImpl;

public class DefaultPartakeAppFactory implements PartakeAppFactory {

    @Override
    public IDBService createDBService() throws Exception{
        return new Postgres9DBService();
    }

    @Override
    public IBitlyService createBitlyService() throws Exception{
        return new BitlyService();
    }

    @Override
    public IEventSearchService createEventSearchService() throws Exception {
        return new EventSearchService();
    }

    @Override
    public ITwitterService createTwitterService() throws Exception{
        return new TwitterService();
    }

    @Override
    public ITestService createTestService() throws Exception {
        return null;
    }

    @Override
    public IOpenIDService createOpenIDService() throws Exception {
        return new OpenIDService();
    }

    @Override
    public IViewInitializer createViewInitializer() throws Exception {
        return new ViewInitializerImpl();
    }
}
