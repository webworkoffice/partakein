package in.partake.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.rosaloves.bitlyj.BitlyException;

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
import in.partake.model.dto.CalendarLinkage;
import in.partake.model.dto.Comment;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.ShortenedURLData;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.resource.PartakeProperties;
import in.partake.util.Util;

public abstract class PartakeService {
    private static final Logger logger = Logger.getLogger(PartakeService.class);
    
    private static PartakeDAOFactory factory;
    private static PartakeConnectionPool pool;
    private static volatile Date bitlyRateLimitExceededTime;
    
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
        User user = getFactory().getUserAccess().find(con, userId);
        if (user == null) { return null; }
        
        // TODO: そのうち、user.getCalendarId() を廃止する予定。
        // とりあえずそれまでは user に書いてある calendarId より、こちらに書いてある calendarId を優先しておく。
        {
            CalendarLinkage linkage = factory.getCalendarAccess().findByUserId(con, userId);
            if (linkage != null) {
                User newUser = new User(user);
                newUser.setCalendarId(linkage.getId());
                newUser.freeze();
                user = newUser;
            }
        }
        
        TwitterLinkage linkage = getFactory().getTwitterLinkageAccess().find(con, String.valueOf(user.getTwitterId()));
        return new UserEx(user, linkage); 
    }
    
    protected EventEx getEventEx(PartakeConnection con, String eventId) throws DAOException {
        Event event = getFactory().getEventAccess().find(con, eventId);
        if (event == null) { return null; }
        UserEx user = getUserEx(con, event.getOwnerId());
        if (user == null) { return null; }
        
        String feedId = getFactory().getFeedAccess().findByEventId(con, eventId);
        String shortenedURL = getShortenedURL(con, event);
        
        List<EventRelation> relations = getFactory().getEventRelationAccess().findByEventId(con, eventId);
        List<EventRelationEx> relationExs = new ArrayList<EventRelationEx>();
        if (relations != null) {
            for (EventRelation relation : relations) {
                EventRelationEx relationEx = getEventRelationEx(con, relation);
                if (relationEx == null) { continue; }
                relationExs.add(relationEx);
            }
        }
        
        return new EventEx(event, user, feedId, shortenedURL, relationExs);
    }

    protected String getShortenedURL(PartakeConnection con, Event event) throws DAOException {
        ShortenedURLData shortenedURLData = getFactory().getURLShortenerAccess().findByURL(con, event.getEventURL());
        if (shortenedURLData == null) {
            Date now = new Date();
            try {
                if (bitlyRateLimitExceededTime == null || now.before(new Date(bitlyRateLimitExceededTime.getTime() + 1000 * 1800))) { // rate limit が出ていたら 30 分待つ。
                    String bitlyShortenedURL = Util.callBitlyShortenURL(event.getEventURL());
                    shortenedURLData = new ShortenedURLData(event.getEventURL(), "bitly", bitlyShortenedURL); 
                    getFactory().getURLShortenerAccess().put(con, shortenedURLData);
                    
                }
            } catch (BitlyException e) {
                // TODO: debugging...
                logger.info(bitlyRateLimitExceededTime != null ? bitlyRateLimitExceededTime : "bitlyRateLimitExceededTime is NULL now.");
                logger.info("now = " + now.toString());
                
                logger.error("failed to shorten URL " + now.toString(), e);
                
                //if (e.getMessage().contains("RATE_LIMIT_EXCEEDED")) {
                    bitlyRateLimitExceededTime = now;
                //}
            }
        }
        
        if (shortenedURLData != null) {
            return shortenedURLData.getShortenedURL();
        } else {
            return null;
        }
    }
    
    protected CommentEx getCommentEx(PartakeConnection con, String commentId) throws DAOException {
    	Comment comment = getFactory().getCommentAccess().find(con, commentId);
    	if (comment == null) { return null; }
    	UserEx user = getUserEx(con, comment.getUserId());
    	if (user == null) { return null; }
    	EventEx event = getEventEx(con, comment.getEventId());
    	if (event == null) { return null; }
    	return new CommentEx(comment, event, user);
    }
    
    protected EventRelationEx getEventRelationEx(PartakeConnection con, EventRelation relation) throws DAOException {
        Event event = getFactory().getEventAccess().find(con, relation.getDstEventId());
        if (event == null) { return null; }
        return new EventRelationEx(relation, event);
    }
    
    protected List<EnrollmentEx> getEnrollmentExs(PartakeConnection con, String eventId) throws DAOException {
        // priority のあるイベントに参加している場合、priority に 1 を付加する。
        
        // --- まず、EnrollmentEx を作成
        List<EnrollmentEx> ps = new ArrayList<EnrollmentEx>();
        for (Enrollment p : factory.getEnrollmentAccess().findByEventId(con, eventId)) {
            if (p == null) { continue; }
            UserEx user = getUserEx(con, p.getUserId());
            if (user == null) { continue; }
            EnrollmentEx pe = new EnrollmentEx(p, user, 0);
            ps.add(pe);
        }
        
        // --- 各 related event に対して、参加しているかどうかを調査。
        List<EventRelation> eventRelations = factory.getEventRelationAccess().findByEventId(con, eventId); 
        for (EventRelation relation : eventRelations) {
            EventEx ev = getEventEx(con, relation.getDstEventId());
            if (ev == null) { continue; }
            
            // related event の参加者を Set で取得
            Set<String> relatedEventParticipantsIds = new HashSet<String>();
            {
                List<Enrollment> relatedEventParticipations = factory.getEnrollmentAccess().findByEventId(con, relation.getDstEventId());
                for (Enrollment p : relatedEventParticipations) {
                    if (p.getStatus().isEnrolled()) {
                        relatedEventParticipantsIds.add(p.getUserId());
                    }
                }
            }
            
            // 参加していれば、それを追加。priority があれば、+1 する。
            for (EnrollmentEx p : ps) {
                if (!relatedEventParticipantsIds.contains(p.getUserId())) { continue; }
                p.addRelatedEventId(relation.getDstEventId());
                if (relation.hasPriority()) {
                    p.setPriority(p.getPriority() + 1);
                }
            }

        }
        
        for (EnrollmentEx p : ps) {
            p.freeze();
        }
        
        Collections.sort(ps, EnrollmentEx.getPriorityBasedComparator());       
        
        return ps;
    }
}
