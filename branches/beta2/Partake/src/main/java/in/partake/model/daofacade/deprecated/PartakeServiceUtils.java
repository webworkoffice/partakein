package in.partake.model.daofacade.deprecated;

import in.partake.base.Util;
import in.partake.model.CommentEx;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.Comment;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.ShortenedURLData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.rosaloves.bitlyj.BitlyException;

/**
 * service 中で共通に使われる関数をまとめたもの。
 * @author shinyak
 *
 */
class PartakeServiceUtils {
    private static final Logger logger = Logger.getLogger(PartakeServiceUtils.class);
    private static volatile Date bitlyRateLimitExceededTime;
    
    public static <T> List<T> convertToList(DataIterator<T> it) throws DAOException {
        try {
            List<T> result = new ArrayList<T>();
            while (it.hasNext()) {
                T t = it.next();
                if (t == null) { continue; }
                result.add(t);
            }
            
            return result;
        } finally {
            it.close();
        }
    }
    
    @Deprecated
    public static UserEx getUserEx(PartakeConnection con, PartakeDAOFactory factory, String userId) throws DAOException {
        return UserDAOFacade.getUserEx(con, userId);
    }
    
    public static EventEx getEventEx(PartakeConnection con, PartakeDAOFactory factory, String eventId) throws DAOException {
        Event event = factory.getEventAccess().find(con, eventId);
        if (event == null) { return null; }
        UserEx user = getUserEx(con, factory, event.getOwnerId());
        if (user == null) { return null; }
        
        String feedId = factory.getEventFeedAccess().findByEventId(con, eventId);
        String shortenedURL = getShortenedURL(con, factory, event);
        
        List<EventRelation> relations = factory.getEventRelationAccess().findByEventId(con, eventId);
        List<EventRelationEx> relationExs = new ArrayList<EventRelationEx>();
        if (relations != null) {
            for (EventRelation relation : relations) {
                EventRelationEx relationEx = getEventRelationEx(con, factory, relation);
                if (relationEx == null) { continue; }
                relationExs.add(relationEx);
            }
        }
        
        return new EventEx(event, user, feedId, shortenedURL, relationExs);
    }

    public static String getShortenedURL(PartakeConnection con, PartakeDAOFactory factory, Event event) throws DAOException {
        ShortenedURLData shortenedURLData = factory.getURLShortenerAccess().findByURL(con, event.getEventURL());
        if (shortenedURLData == null) {
            Date now = new Date();
            try {
                if (bitlyRateLimitExceededTime == null || now.before(new Date(bitlyRateLimitExceededTime.getTime() + 1000 * 1800))) { // rate limit が出ていたら 30 分待つ。
                    String bitlyShortenedURL = Util.callBitlyShortenURL(event.getEventURL());
                    shortenedURLData = new ShortenedURLData(event.getEventURL(), "bitly", bitlyShortenedURL); 
                    factory.getURLShortenerAccess().put(con, shortenedURLData);
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
    
    public static CommentEx getCommentEx(PartakeConnection con, PartakeDAOFactory factory, String commentId) throws DAOException {
        Comment comment = factory.getCommentAccess().find(con, commentId);
        if (comment == null) { return null; }
        UserEx user = getUserEx(con, factory, comment.getUserId());
        if (user == null) { return null; }
        EventEx event = getEventEx(con, factory, comment.getEventId());
        if (event == null) { return null; }
        return new CommentEx(comment, event, user);
    }
    
    public static EventRelationEx getEventRelationEx(PartakeConnection con, PartakeDAOFactory factory, EventRelation relation) throws DAOException {
        Event event = factory.getEventAccess().find(con, relation.getDstEventId());
        if (event == null) { return null; }
        return new EventRelationEx(relation, event);
    }
    
    public static List<EnrollmentEx> getEnrollmentExs(PartakeConnection con, PartakeDAOFactory factory, String eventId) throws DAOException {
        // priority のあるイベントに参加している場合、priority に 1 を付加する。
        
        // --- まず、EnrollmentEx を作成
        List<EnrollmentEx> ps = new ArrayList<EnrollmentEx>();
        for (Enrollment p : factory.getEnrollmentAccess().findByEventId(con, eventId)) {
            if (p == null) { continue; }
            UserEx user = getUserEx(con, factory, p.getUserId());
            if (user == null) { continue; }
            EnrollmentEx pe = new EnrollmentEx(p, user, 0);
            ps.add(pe);
        }
        
        // --- 各 related event に対して、参加しているかどうかを調査。
        List<EventRelation> eventRelations = factory.getEventRelationAccess().findByEventId(con, eventId); 
        for (EventRelation relation : eventRelations) {
            EventEx ev = getEventEx(con, factory, relation.getDstEventId());
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
