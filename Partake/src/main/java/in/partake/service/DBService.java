package in.partake.service;

import org.apache.log4j.Logger;

import in.partake.base.PartakeRuntimeException;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeConnectionPool;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.daofacade.AbstractPartakeDAOFacade;
import in.partake.resource.PartakeProperties;
import in.partake.resource.ServerErrorCode;

public final class DBService {
    private static final Logger logger = Logger.getLogger(DBService.class);

    private static PartakeConnectionPool pool;
    private static PartakeDAOFactory factory;

    private DBService() {
        // Prevents from instantiation.
    }
    
    public static PartakeConnectionPool getPool() {
        return pool;
    }
    
    public static PartakeDAOFactory getFactory() {
        return factory;
    }

    /** reset database connection. Call this carefully. */
    public static void initialize() {
        try {
            initializeConnectionPool();

            Class<?> factoryClass = Class.forName(PartakeProperties.get().getDAOFactoryClassName());
            factory = (PartakeDAOFactory) factoryClass.newInstance();

            PartakeConnection con = getPool().getConnection();
            try {
                factory.initialize(con);
                AbstractPartakeDAOFacade.setFactory(factory);
            } finally {
                con.invalidate();
            }
        } catch (ClassNotFoundException e) {
            logger.fatal("Specified factory or pool class doesn't exist.", e);
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            logger.fatal("Failed to create instance of specified factory or pool.", e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            logger.fatal("Illegal access.", e);
            throw new RuntimeException(e);
        } catch (DAOException e) {
            logger.fatal("DAOException", e);
            throw new PartakeRuntimeException(ServerErrorCode.DAO_INITIALIZATION_ERROR, e);
        }
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
