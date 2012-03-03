package in.partake.service;

import in.partake.model.CommentEx;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeConnectionPool;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventRelation;

import java.util.List;

import org.apache.log4j.Logger;

public abstract class PartakeService {
    @Deprecated
    protected PartakeDAOFactory getFactory() {
        return DatabaseService.getFactory();
    }
    
    @Deprecated
    protected PartakeConnectionPool getPool() {
        return DatabaseService.getPool();
    }
    
    // ----------------------------------------------------------------------
    // Utility functions

    protected static <T> List<T> convertToList(DataIterator<T> it) throws DAOException {
        return PartakeServiceUtils.convertToList(it);
    }

    protected UserEx getUserEx(PartakeConnection con, String userId) throws DAOException {
        return PartakeServiceUtils.getUserEx(con, getFactory(), userId);
    }

    protected EventEx getEventEx(PartakeConnection con, String eventId) throws DAOException {
        return PartakeServiceUtils.getEventEx(con, getFactory(), eventId);
    }

    protected String getShortenedURL(PartakeConnection con, Event event) throws DAOException {
        return PartakeServiceUtils.getShortenedURL(con, getFactory(), event);
    }

    protected CommentEx getCommentEx(PartakeConnection con, String commentId) throws DAOException {
        return PartakeServiceUtils.getCommentEx(con, getFactory(), commentId);
    }

    protected EventRelationEx getEventRelationEx(PartakeConnection con, EventRelation relation) throws DAOException {
        return PartakeServiceUtils.getEventRelationEx(con, getFactory(), relation);
    }

    protected List<EnrollmentEx> getEnrollmentExs(PartakeConnection con, String eventId) throws DAOException {
        return PartakeServiceUtils.getEnrollmentExs(con, getFactory(), eventId);
    }
}
