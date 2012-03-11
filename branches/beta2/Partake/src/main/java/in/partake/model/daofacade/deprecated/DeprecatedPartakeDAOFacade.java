package in.partake.model.daofacade.deprecated;

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
import in.partake.model.daoutil.DAOUtil;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventRelation;
import in.partake.service.DBService;

import java.util.List;

public abstract class DeprecatedPartakeDAOFacade {
    @Deprecated
    protected PartakeDAOFactory getFactory() {
        return DBService.getFactory();
    }
    
    @Deprecated
    protected PartakeConnectionPool getPool() {
        return DBService.getPool();
    }
    
    // ----------------------------------------------------------------------
    // Utility functions

    protected static <T> List<T> convertToList(DataIterator<T> it) throws DAOException {
        return DAOUtil.convertToList(it);
    }

    protected UserEx getUserEx(PartakeConnection con, String userId) throws DAOException {
        return DeprecatedPartakeDAOFacadeUtils.getUserEx(con, getFactory(), userId);
    }

    protected EventEx getEventEx(PartakeConnection con, String eventId) throws DAOException {
        return DeprecatedPartakeDAOFacadeUtils.getEventEx(con, getFactory(), eventId);
    }

    protected String getShortenedURL(PartakeConnection con, Event event) throws DAOException {
        return DeprecatedPartakeDAOFacadeUtils.getShortenedURL(con, getFactory(), event);
    }

    protected CommentEx getCommentEx(PartakeConnection con, String commentId) throws DAOException {
        return DeprecatedPartakeDAOFacadeUtils.getCommentEx(con, getFactory(), commentId);
    }

    protected EventRelationEx getEventRelationEx(PartakeConnection con, EventRelation relation) throws DAOException {
        return DeprecatedPartakeDAOFacadeUtils.getEventRelationEx(con, getFactory(), relation);
    }

    protected List<EnrollmentEx> getEnrollmentExs(PartakeConnection con, String eventId) throws DAOException {
        return DeprecatedPartakeDAOFacadeUtils.getEnrollmentExs(con, getFactory(), eventId);
    }
}
