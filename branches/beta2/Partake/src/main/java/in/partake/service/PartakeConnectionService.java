package in.partake.service;

import in.partake.model.dao.PartakeConnectionPool;
import in.partake.resource.PartakeProperties;

import org.apache.log4j.Logger;

/**
 * A service class which provides getPool().
 * All classes which gets connection should inherit this class.
 * 
 * @author shinyak
 *
 */
public abstract class PartakeConnectionService {
    private static final Logger logger = Logger.getLogger(PartakeConnectionService.class);

    private static PartakeConnectionPool pool;

    protected static PartakeConnectionPool getPool() {
        return pool;
    }

    public static void initializeConnectionPool() {
        try {
            if (pool != null)
                pool.willDestroy();

            Class<?> poolClass = Class.forName(PartakeProperties.get().getConnectionPoolClassName());
            pool = (PartakeConnectionPool) poolClass.newInstance();
        } catch (ClassNotFoundException e) {
            logger.fatal("Specified factory or pool class doesn't exist.", e);
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            logger.fatal("Failed to create instance of specified factory or pool.", e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            logger.fatal("Illegal access.", e);
            throw new RuntimeException(e);
        }
    }
}
