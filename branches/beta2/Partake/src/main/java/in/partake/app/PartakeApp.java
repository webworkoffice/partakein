package in.partake.app;

import in.partake.resource.PartakeProperties;
import in.partake.service.IBitlyService;
import in.partake.service.IDBService;
import in.partake.service.IEventSearchService;
import in.partake.service.IOpenIDService;
import in.partake.service.IDaemonInitializer;
import in.partake.service.ITestService;
import in.partake.service.ITwitterService;
import in.partake.service.IViewInitializer;

import org.apache.log4j.Logger;

public class PartakeApp {
    private static final Logger logger = Logger.getLogger(PartakeApp.class);

    protected static IDBService dbService;
    protected static IBitlyService bitlyService;
    protected static IEventSearchService eventSearchService;
    protected static ITwitterService twitterService;
    protected static ITestService testService;
    protected static IOpenIDService openIDService;

    // Initializers
    private static IViewInitializer viewInitializer;
    private static IDaemonInitializer daemonInitializer;

    public static void initialize() throws Exception {
        PartakeProperties.get().reset();
        createServices();
    }

    public static void initialize(String mode) throws Exception {
        PartakeProperties.get().reset(mode);
        createServices();
    }

    private static void createServices() throws Exception {
        try {
            Class<?> factoryClass = Class.forName(PartakeProperties.get().getPartakeAppFactoryClassName());
            PartakeAppFactory factory = (PartakeAppFactory) factoryClass.newInstance();

            createServices(factory);
        } catch (ClassNotFoundException e) {
            logger.fatal("ClassNotFoundException", e);
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            logger.fatal("InstantiationException", e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            logger.fatal("IllegalAccessException", e);
            throw new RuntimeException(e);
        }
    }

    private static void createServices(PartakeAppFactory factory) throws Exception {
        testService = factory.createTestService();
        if (testService != null)
            testService.initialize();

        dbService = factory.createDBService();
        bitlyService = factory.createBitlyService();
        eventSearchService = factory.createEventSearchService();
        twitterService = factory.createTwitterService();
        openIDService = factory.createOpenIDService();
        viewInitializer = factory.createViewInitializer();
        daemonInitializer = factory.createDaemonInitializer();

        if (dbService != null)
            dbService.initialize();
        if (daemonInitializer != null)
            daemonInitializer.initialize();
    }

    public static IDBService getDBService() {
        return dbService;
    }

    public static IBitlyService getBitlyService() {
        return bitlyService;
    }

    public static IEventSearchService getEventSearchService() {
        return eventSearchService;
    }

    public static ITwitterService getTwitterService() {
        return twitterService;
    }

    public static ITestService getTestService() {
        return testService;
    }

    public static IOpenIDService getOpenIDService() {
        return openIDService;
    }

    public static IViewInitializer getViewInitializer() {
        return viewInitializer;
    }

    public static IDaemonInitializer getDaemonInitializer() {
        return daemonInitializer;
    }
}
