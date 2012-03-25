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
    public IDBService createDBService() {
        return new Postgres9DBService();
    }

    @Override
    public IBitlyService createBitlyService() {
        return new BitlyService();
    }

    @Override
    public IEventSearchService createEventSearchService() {
        return new EventSearchService();
    }

    @Override
    public ITwitterService createTwitterService() {
        return new TwitterService();
    }

    @Override
    public ITestService createTestService() {
        return null;
    }

    @Override
    public IOpenIDService createOpenIDService() {
        return new OpenIDService();
    }

    @Override
    public IViewInitializer createViewInitializer() {
        return new ViewInitializerImpl();
    }
}
