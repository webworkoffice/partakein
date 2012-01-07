package in.partake.service;

import java.util.List;

import org.apache.log4j.Logger;

import in.partake.model.CommentEx;
import in.partake.model.EventEx;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventRelationEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeConnectionPool;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventRelation;
import in.partake.resource.PartakeProperties;

public abstract class PartakeService {
    private static PartakeDAOFactory factory;
    private static PartakeConnectionPool pool;
    private static final Logger logger = Logger.getLogger(PartakeService.class);

    static {
        reset();
    }

    protected static PartakeDAOFactory getFactory() {
        return factory;
    }

    protected static PartakeConnectionPool getPool() {
        return pool;
    }

    /** reset database connection. Call this carefully. */
    public static void reset() {
        try {
            if (pool != null)
                pool.willDestroy();
            
            Class<?> factoryClass = Class.forName(PartakeProperties.get().getDAOFactoryClassName());
            factory = (PartakeDAOFactory) factoryClass.newInstance();
            
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


    // ----------------------------------------------------------------------
    // Utility functions

    protected static <T> List<T> convertToList(DataIterator<T> it) throws DAOException {
        return PartakeServiceUtils.convertToList(it);
    }

    protected UserEx getUserEx(PartakeConnection con, String userId) throws DAOException {
        return PartakeServiceUtils.getUserEx(con, factory, userId);
    }

    protected EventEx getEventEx(PartakeConnection con, String eventId) throws DAOException {
        return PartakeServiceUtils.getEventEx(con, factory, eventId);
    }

    protected String getShortenedURL(PartakeConnection con, Event event) throws DAOException {
        return PartakeServiceUtils.getShortenedURL(con, factory, event);
    }

    protected CommentEx getCommentEx(PartakeConnection con, String commentId) throws DAOException {
        return PartakeServiceUtils.getCommentEx(con, factory, commentId);
    }

    protected EventRelationEx getEventRelationEx(PartakeConnection con, EventRelation relation) throws DAOException {
        return PartakeServiceUtils.getEventRelationEx(con, factory, relation);
    }

    protected List<EnrollmentEx> getEnrollmentExs(PartakeConnection con, String eventId) throws DAOException {
        return PartakeServiceUtils.getEnrollmentExs(con, factory, eventId);
    }
}
