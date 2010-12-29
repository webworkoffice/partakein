package in.partake.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.partake.model.CommentEx;
import in.partake.model.EventEx;
import in.partake.model.ParticipationEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeConnectionPool;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.Comment;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.Participation;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.resource.PartakeProperties;

public abstract class PartakeService {
    private static final PartakeDAOFactory factory;
    private static final PartakeConnectionPool pool;
    
    static {
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
    
    protected static PartakeDAOFactory getFactory() {
        return factory;
    }
    
    protected static PartakeConnectionPool getPool() {
        return pool;
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
    
    // TODO: これがここにいるのはなんかおかしいような気がする。階層化が足りないのではないか。
    
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
    
    protected List<ParticipationEx> getParticipationsEx(PartakeConnection con, String eventId) throws DAOException {
        // priority のあるイベントに参加している場合、priority に 1 を負荷する。
        Map<String, Integer> priorityMap = new HashMap<String, Integer>();
        
        List<EventRelation> eventRelations = factory.getEventRelationAccess().getEventRelations(con, eventId); 
        for (EventRelation relation : eventRelations) {
            if (!relation.hasPriority()) { continue; }
            EventEx ev = getEventEx(con, relation.getEventId()); 
            if (ev == null) { continue; }
            List<Participation> ps = factory.getEnrollmentAccess().getParticipation(con, relation.getEventId());
            for (Participation p : ps) {
                if (!p.getStatus().isEnrolled()) { continue; }
                if (priorityMap.containsKey(p.getUserId())) {
                    priorityMap.put(p.getUserId(), priorityMap.get(p.getUserId()) + 1);
                } else {
                    priorityMap.put(p.getUserId(), Integer.valueOf(1));
                }
            }
        }
        
        List<Participation> ps = factory.getEnrollmentAccess().getParticipation(con, eventId);
        List<ParticipationEx> result = new ArrayList<ParticipationEx>(); 
        for (Participation p : ps) {
            UserEx user = getUserEx(con, p.getUserId()); 
            ParticipationEx pe = new ParticipationEx(p, user);
            if (priorityMap.containsKey(p.getUserId())) {
                pe.setPriority(priorityMap.get(p.getUserId())); // TODO: 元の priority にたさないといけないんじゃないか？
            }
            pe.freeze();
            result.add(pe);
        }
        
        Collections.sort(result, Participation.getPriorityBasedComparator());       
        
        return result;
    }
}
