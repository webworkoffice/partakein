package in.partake.service;

import java.util.List;
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
    
    static {
        reset();
    }
    
    protected static PartakeDAOFactory getFactory() {
        return factory;
    }
    
    protected static PartakeConnectionPool getPool() {
        return pool;
    }
    
    protected static void reset() {
        try {
            Class<?> factoryClass = Class.forName(PartakeProperties.get().getDAOFactoryClassName());
            factory = (PartakeDAOFactory) factoryClass.newInstance();
            
            Class<?> poolClass = Class.forName(PartakeProperties.get().getConnectionPoolClassName());
            pool = (PartakeConnectionPool) poolClass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
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
