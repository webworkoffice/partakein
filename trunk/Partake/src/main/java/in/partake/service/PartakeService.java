package in.partake.service;

import java.util.ArrayList;
import java.util.List;

import in.partake.model.CommentEx;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeModelFactory;
import in.partake.model.dao.cassandra.CassandraDAOFactory;
import in.partake.model.dto.Comment;
import in.partake.model.dto.Event;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;

public abstract class PartakeService {
    private static PartakeModelFactory factory = new CassandraDAOFactory();
    
    public static void setDAOFactory(PartakeModelFactory factory) {
        PartakeService.factory = factory;
    }
    
    protected static PartakeModelFactory getFactory() {
        return factory;
    }
    
    // ----------------------------------------------------------------------
    // Utility functions
    
    protected static <T> List<T> convertToList(DataIterator<T> it) throws DAOException {
        List<T> result = new ArrayList<T>();
        while (it.hasNext()) {
            T t = it.next();
            if (t == null) { continue; }
            result.add(t);
        }
        
        return result;
    }
    
    protected UserEx getUserEx(PartakeConnection con, String userId) throws DAOException {
        User user = getFactory().getUserAccess().getUserById(con, userId);
        if (user == null) { return null; }
        TwitterLinkage linkage = getFactory().getTwitterLinkageAccess().getTwitterLinkageById(con, user.getTwitterId());
        return new UserEx(user, linkage); 
    }
    
    protected EventEx getEventEx(PartakeConnection con, String eventId) throws DAOException {
        Event event = getFactory().getEventAccess().getEventById(con, eventId);
        if (event == null) { return null; }
        UserEx user = getUserEx(con, event.getOwnerId());
        if (user == null) { return null; }
        
        String feedId = getFactory().getFeedAccess().getFeedIdByEventId(con, eventId);        
        return new EventEx(event, user, feedId);
    }
    
    protected CommentEx getCommentEx(PartakeConnection con, String commentId) throws DAOException {
    	Comment comment = getFactory().getCommentAccess().getCommentById(con, commentId);
    	if (comment == null) { return null; }
    	UserEx user = getUserEx(con, comment.getUserId());
    	if (user == null) { return null; }
    	EventEx event = getEventEx(con, comment.getEventId());
    	if (event == null) { return null; }
    	return new CommentEx(comment, event, user);
    }
}
