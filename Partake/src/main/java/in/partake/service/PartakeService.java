package in.partake.service;

import in.partake.resource.PartakeProperties;

import org.apache.log4j.Logger;

public abstract class PartakeService {
    private static final Logger logger = Logger.getLogger(PartakeService.class);
    private static PartakeService instance;
    
    private final IBitlyService bitlyService;
    private final IEventSearchService eventSearchService;
    private final ITwitterService twitterService;

    public static PartakeService get() {
        return instance;
    }
    
    public static void initialize() {
        try {
            Class<?> factoryClass = Class.forName(PartakeProperties.get().getPartakeServiceFactoryClassName());
            instance = (PartakeService) factoryClass.newInstance();
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
    
    protected PartakeService() {
        bitlyService = createBitlyService();
        eventSearchService = createEventSearchService();
        twitterService = createTwitterService();
    }
    
    protected abstract IBitlyService createBitlyService();
    protected abstract IEventSearchService createEventSearchService();
    protected abstract ITwitterService createTwitterService();
    
    public IBitlyService getBitlyService() {
        return bitlyService;
    }
    
    public IEventSearchService getEventSearchService() {
        return eventSearchService;
    }
    
    public ITwitterService getTwitterService() {
        return twitterService;
    }
}
