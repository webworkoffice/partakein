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
    
    /**
     * PartakeService に必要なデータを読み直す。最初の初期化とユニットテスト用途のみを想定。
     * TODO: public にしたくないんだけど、どうしようか... reflection でも使うしか...。
     * 
     */
    public static void reset() {
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
        String shortenedURL = getFactory().getURLShortenerAccess().getShortenedURL(con, event.getEventURL());
        if (shortenedURL == null) {
            Date now = new Date();
            try {
                
                if (bitlyRateLimitExceededTime == null || now.before(new Date(bitlyRateLimitExceededTime.getTime() + 1000 * 1800))) { // rate limit が出ていたら 30 分待つ。
                    String bitlyShortenedURL = Util.callBitlyShortenURL(event.getEventURL());
                    getFactory().getURLShortenerAccess().addShortenedURL(con, event.getEventURL(), "bitly", bitlyShortenedURL);
                    shortenedURL = bitlyShortenedURL;
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
        return new EventEx(event, user, feedId, shortenedURL);
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
        // priority のあるイベントに参加している場合、priority に 1 を付加する。
        
        // --- まず、ParticipationEx を作成
        List<ParticipationEx> ps = new ArrayList<ParticipationEx>();
        for (Participation p : factory.getEnrollmentAccess().getParticipation(con, eventId)) {
            if (p == null) { continue; }
            UserEx user = getUserEx(con, p.getUserId());
            if (user == null) { continue; }
            ParticipationEx pe = new ParticipationEx(p, user);
            ps.add(pe);
        }
        
        // --- 各 related event に対して、参加しているかどうかを調査。
        List<EventRelation> eventRelations = factory.getEventRelationAccess().getEventRelations(con, eventId); 
        for (EventRelation relation : eventRelations) {
            EventEx ev = getEventEx(con, relation.getEventId());
            if (ev == null) { continue; }
            
            // related event の参加者を Set で取得
            Set<String> relatedEventParticipantsIds = new HashSet<String>();
            {
                List<Participation> relatedEventParticipations = factory.getEnrollmentAccess().getParticipation(con, relation.getEventId());
                for (Participation p : relatedEventParticipations) {
                    if (p.getStatus().isEnrolled()) {
                        relatedEventParticipantsIds.add(p.getUserId());
                    }
                }
            }
            
            // 参加していれば、それを追加。priority があれば、+1 する。
            for (ParticipationEx p : ps) {
                if (!relatedEventParticipantsIds.contains(p.getUserId())) { continue; }
                p.addRelatedEventId(relation.getEventId());
                if (relation.hasPriority()) {
                    p.setPriority(p.getPriority() + 1);
                }
            }

        }
        
        for (ParticipationEx p : ps) {
            p.freeze();
        }
        
        Collections.sort(ps, Participation.getPriorityBasedComparator());       
        
        return ps;
    }
}
