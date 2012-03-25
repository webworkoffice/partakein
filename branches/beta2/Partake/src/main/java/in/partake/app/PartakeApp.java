package in.partake.app;

import in.partake.base.PartakeException;
import in.partake.model.dao.DAOException;
import in.partake.resource.PartakeProperties;
import in.partake.service.IBitlyService;
import in.partake.service.IDBService;
import in.partake.service.IEventSearchService;
import in.partake.service.IOpenIDService;
import in.partake.service.ITestService;
import in.partake.service.ITwitterService;

import org.apache.log4j.Logger;

public class PartakeApp {
    private static final Logger logger = Logger.getLogger(PartakeApp.class);

    private static IDBService dbService;
    private static IBitlyService bitlyService;
    private static IEventSearchService eventSearchService;
    private static ITwitterService twitterService;
    private static ITestService testService;
    private static IOpenIDService openIDService;

    public static void initialize() throws DAOException, PartakeException {
        PartakeProperties.get().reset();
        createServices();
    }

    public static void initialize(String mode) throws DAOException, PartakeException {
        PartakeProperties.get().reset(mode);
        createServices();
    }

    private static void createServices() throws DAOException, PartakeException {
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

    private static void createServices(PartakeAppFactory factory) throws DAOException, PartakeException {
        testService = factory.createTestService();
        if (testService != null)
            testService.initialize();

        dbService = factory.createDBService();
        bitlyService = factory.createBitlyService();
        eventSearchService = factory.createEventSearchService();
        twitterService = factory.createTwitterService();
        openIDService = factory.createOpenIDService();

        if (dbService != null)
            dbService.initialize();
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
}
