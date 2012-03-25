package in.partake.service;

import in.partake.resource.PartakeProperties;

import org.apache.log4j.Logger;

public abstract class PartakeService {
    private static final Logger logger = Logger.getLogger(PartakeService.class);
    private static PartakeService instance;
    private final IBitlyService bitlyService;

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
    }
    
    protected abstract IBitlyService createBitlyService();
    
    public IBitlyService getBitlyService() {
        return bitlyService;
    }
}
