package in.partake.model.daofacade;

import in.partake.base.Util;
import in.partake.model.CommentEx;
import in.partake.model.DirectMessageEx;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dao.access.IEventRelationAccess;
import in.partake.model.dto.Comment;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventActivity;
import in.partake.model.dto.EventFeedLinkage;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.Message;
import in.partake.model.dto.ShortenedURLData;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.pk.EnrollmentPK;
import in.partake.resource.PartakeProperties;
import in.partake.service.EventSearchServiceException;
import in.partake.service.IEventSearchService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class EventDAOFacade {
    private static final Logger logger = Logger.getLogger(EventDAOFacade.class);

    public static EventEx getEventEx(PartakeConnection con, IPartakeDAOs daos, String eventId) throws DAOException {
        Event event = daos.getEventAccess().find(con, eventId);
        if (event == null) { return null; }
        UserEx user = UserDAOFacade.getUserEx(con, daos, event.getOwnerId());
        if (user == null) { return null; }

        String feedId = daos.getEventFeedAccess().findByEventId(con, eventId);
        String shortenedURL = getShortenedURL(con, daos, event);

        List<EventRelation> relations = daos.getEventRelationAccess().findByEventId(con, eventId);
        List<EventRelationEx> relationExs = new ArrayList<EventRelationEx>();
        if (relations != null) {
            for (EventRelation relation : relations) {
                EventRelationEx relationEx = getEventRelationEx(con, daos, relation);
                if (relationEx == null) { continue; }
                relationExs.add(relationEx);
            }
        }

        return new EventEx(event, user, feedId, shortenedURL, relationExs);
    }

    public static EventRelationEx getEventRelationEx(PartakeConnection con, IPartakeDAOs daos, EventRelation relation) throws DAOException {
        Event event = daos.getEventAccess().find(con, relation.getDstEventId());
        if (event == null) { return null; }
        return new EventRelationEx(relation, event);
    }

    public static String getShortenedURL(PartakeConnection con, IPartakeDAOs daos, Event event) throws DAOException {
        // TODO: Connection 掴んだまま BitlyService 呼び出すとか狂気の沙汰すぎる。
        ShortenedURLData shortenedURLData = daos.getURLShortenerAccess().findByURL(con, event.getEventURL());
//        if (shortenedURLData == null) {
//            Date now = new Date();
//            try {
//                if (bitlyRateLimitExceededTime == null || now.before(new Date(bitlyRateLimitExceededTime.getTime() + 1000 * 1800))) { // rate limit が出ていたら 30 分待つ。
//                    String bitlyShortenedURL = BitlyService.callBitlyShortenURL(event.getEventURL());
//                    shortenedURLData = new ShortenedURLData(event.getEventURL(), "bitly", bitlyShortenedURL);
//                    factory.getURLShortenerAccess().put(con, shortenedURLData);
//                }
//            } catch (BitlyException e) {
//                // TODO: debugging...
//                logger.info(bitlyRateLimitExceededTime != null ? bitlyRateLimitExceededTime : "bitlyRateLimitExceededTime is NULL now.");
//                logger.info("now = " + now.toString());
//
//                logger.error("failed to shorten URL " + now.toString(), e);
//
//                //if (e.getMessage().contains("RATE_LIMIT_EXCEEDED")) {
//                    bitlyRateLimitExceededTime = now;
//                //}
//            }
//        }

        if (shortenedURLData != null) {
            return shortenedURLData.getShortenedURL();
        } else {
            return null;
        }
    }


    /**
     * event をデータベースに保持します。
     * @return event id
     */
    public static String create(PartakeConnection con, IPartakeDAOs daos, Event eventEmbryo) throws DAOException {
        String eventId = daos.getEventAccess().getFreshId(con);
        eventEmbryo.setId(eventId);
        daos.getEventAccess().put(con, eventEmbryo);

        // Feed Dao にも挿入。
        String feedId = daos.getEventFeedAccess().findByEventId(con, eventId);
        if (feedId == null) {
            feedId = daos.getEventFeedAccess().getFreshId(con);
            daos.getEventFeedAccess().put(con, new EventFeedLinkage(feedId, eventId));
        }

        // Event Activity にも挿入
        {
            IEventActivityAccess eaa = daos.getEventActivityAccess();
            EventActivity activity = new EventActivity(eaa.getFreshId(con), eventEmbryo.getId(), "イベントが登録されました : " + eventEmbryo.getTitle(), eventEmbryo.getDescription(), eventEmbryo.getCreatedAt());
            eaa.put(con, activity);
        }

        // さらに、twitter bot がつぶやく (private の場合はつぶやかない)
        if (!eventEmbryo.isPrivate() && !eventEmbryo.isPreview()) {
            tweetNewEventArrival(con, daos, eventEmbryo);
        }

        return eventEmbryo.getId();
    }

    public static void modify(PartakeConnection con, IPartakeDAOs daos, Event eventEmbryo) throws DAOException {
        assert eventEmbryo != null;
        assert eventEmbryo.getId() != null;

        // master を update
        daos.getEventAccess().put(con, eventEmbryo);

        // Event Activity にも挿入
        {
            IEventActivityAccess eaa = daos.getEventActivityAccess();
            EventActivity activity = new EventActivity(eaa.getFreshId(con), eventEmbryo.getId(), "イベントが更新されました : " + eventEmbryo.getTitle(), eventEmbryo.getDescription(), eventEmbryo.getCreatedAt());
            eaa.put(con, activity);
        }

        // TODO: twitter bot が更新をつぶやいてもいいような気がする。
    }

    public static List<EventRelationEx> getEventRelationsEx(PartakeConnection con, IPartakeDAOs daos, Event event) throws DAOException {
        List<EventRelationEx> relations = new ArrayList<EventRelationEx>();
        for (EventRelation relation : daos.getEventRelationAccess().findByEventId(con, event.getId())) {
            if (relation == null)
                continue;

            EventRelationEx relex = new EventRelationEx(relation, event);
            relex.freeze();
            relations.add(relex);
        }

        return relations;

    }

    public static void setEventRelations(PartakeConnection con, IPartakeDAOs daos, String eventId, List<EventRelation> relations) throws DAOException {
        IEventRelationAccess dao = daos.getEventRelationAccess();

        // NOTE: こういうふうにやると、Cassandra の場合 remove が優先されてしまう。
        //            con.beginTransaction();
        //            factory.getEventRelationAccess().removeByEventId(con, eventId);
        //            for (EventRelation er : relations) {
        //                assert (eventId.equals(er.getSrcEventId()));
        //                factory.getEventRelationAccess().put(con, er);
        //            }
        //          con.commit();

        // 古いものを update/remove
        List<EventRelation> oldRelations = dao.findByEventId(con, eventId);
        for (EventRelation er : oldRelations) {
            boolean found = false;
            for (int i = 0; i < relations.size(); ++i) {
                if (relations.get(i) == null)
                    continue;
                if (relations.get(i).getPrimaryKey().equals(er.getPrimaryKey())) {
                    found = true;
                    dao.put(con, relations.get(i));
                    relations.set(i, null);
                    break;
                }
            }

            if (!found)
                dao.remove(con, er.getPrimaryKey());
        }

        // 新しいものを insert
        for (int i = 0; i < relations.size(); ++i) {
            EventRelation er = relations.get(i);
            if (er == null) { continue; }
            dao.put(con, er);
        }
    }


    public static void recreateEventIndex(PartakeConnection con, IPartakeDAOs daos, IEventSearchService searchService) throws DAOException, EventSearchServiceException {
        searchService.truncate();
        DataIterator<Event> it = daos.getEventAccess().getIterator(con);
        try {
            while (it.hasNext()) {
                Event event = it.next();
                if (event == null) { continue; }
                if (event.isPrivate() || event.isPreview())
                    searchService.remove(event.getId());
                else if (searchService.hasIndexed(event.getId()))
                    searchService.update(event);
                else
                    searchService.create(event);
            }
        } finally {
            it.close();
        }
    }

    /** user が event に登録するために、登録が必要な event たちを列挙する。 */
    public static List<Event> getRequiredEventsNotEnrolled(PartakeConnection con, IPartakeDAOs daos, UserEx user, List<EventRelationEx> relations) throws DAOException {
        List<Event> requiredEvents = new ArrayList<Event>();
        for (EventRelationEx relation : relations) {
            if (!relation.isRequired())
                continue;

            if (relation.getEvent() == null)
                continue;

            // If <code>user</code> is null, we mark this event as not enrolled.
            if (user == null) {
                requiredEvents.add(relation.getEvent());
                continue;
            }

            Enrollment enrollment = daos.getEnrollmentAccess().find(con, new EnrollmentPK(user.getId(), relation.getEvent().getId()));
            ParticipationStatus status = enrollment != null ? enrollment.getStatus() : ParticipationStatus.NOT_ENROLLED;

            if (status.isEnrolled())
                continue;

            requiredEvents.add(relation.getEvent());
        }

        return requiredEvents;
    }

    // ----------------------------------------------------------------------
    // Comments

    public static CommentEx getCommentEx(PartakeConnection con, IPartakeDAOs daos, String commentId) throws DAOException {
        Comment comment = daos.getCommentAccess().find(con, commentId);
        if (comment == null) { return null; }
        UserEx user = UserDAOFacade.getUserEx(con, daos, comment.getUserId());
        if (user == null) { return null; }
        return new CommentEx(comment, user);
    }

    public static List<CommentEx> getCommentsExByEvent(PartakeConnection con, IPartakeDAOs daos, String eventId) throws DAOException {
        List<CommentEx> result = new ArrayList<CommentEx>();

        con.beginTransaction();
        DataIterator<Comment> iterator = daos.getCommentAccess().getCommentsByEvent(con, eventId);
        try {
            if (iterator == null) { return result; }

            while (iterator.hasNext()) {
                Comment comment = iterator.next();
                if (comment == null) { continue; }
                String commentId = comment.getId();
                if (commentId == null) { continue; }
                CommentEx commentEx = getCommentEx(con, daos, commentId);
                if (commentEx == null) { continue; }
                result.add(commentEx);
            }
        } finally {
            iterator.close();
        }

        return result;
    }

    /**
     * ある event で管理者がユーザーに送ったメッセージを送った順に取得する。
     * @param eventId
     * @return
     * @throws DAOException
     */
    public static List<DirectMessageEx> getUserMessagesByEventId(PartakeConnection con, IPartakeDAOs daos, String eventId) throws DAOException {
        List<DirectMessageEx> messages = new ArrayList<DirectMessageEx>();
        DataIterator<Message> it = daos.getDirectMessageAccess().findByEventId(con, eventId);
        try {
            while (it.hasNext()) {
                Message message = it.next();
                messages.add(new DirectMessageEx(message, UserDAOFacade.getUserEx(con, daos, message.getUserId())));
            }
        } finally {
            it.close();
        }

        return messages;
    }

    private static void tweetNewEventArrival(PartakeConnection con, IPartakeDAOs daos, Event event) throws DAOException {
        String shortenedURL = null;
        ShortenedURLData shortenedURLData = daos.getURLShortenerAccess().findByURL(con, event.getEventURL());
        if (shortenedURLData != null)
            shortenedURL = shortenedURLData.getServiceType();
        else
            shortenedURL = event.getEventURL();

        String hashTag = event.getHashTag() != null ? event.getHashTag() : "";
        String messagePrefix = "[PARTAKE] 新しいイベントが追加されました :";
        int length = (messagePrefix.length() + 1) + (shortenedURL.length() + 1) + (hashTag.length() + 1);
        String title = Util.shorten(event.getTitle(), 140 - length);

        String message = messagePrefix + " " + title + " " + shortenedURL + " " + hashTag;
        int twitterId = PartakeProperties.get().getTwitterBotTwitterId();
        if (twitterId < 0) {
            logger.info("No bot id.");
            return;
        }
        TwitterLinkage linkage = daos.getTwitterLinkageAccess().find(con, String.valueOf(twitterId));
        if (linkage == null) {
            logger.info("twitter bot does have partake user id. Login using the account once to create the user id.");
            return;
        }
        String userId = linkage.getUserId();
        if (userId == null) {
            logger.info("twitter bot does have partake user id. Login using the account once to create the user id.");
            return;
        }

        String messageId = daos.getDirectMessageAccess().getFreshId(con);
        Message embryo = new Message(messageId, userId, message, null, new Date());
        daos.getDirectMessageAccess().put(con, embryo);
        String envelopeId = daos.getEnvelopeAccess().getFreshId(con);
        Envelope envelope = new Envelope(envelopeId, userId, null, messageId, null, 0, null, null, DirectMessagePostingType.POSTING_TWITTER, new Date());
        daos.getEnvelopeAccess().put(con, envelope);

        logger.info("bot will tweet: " + message);
    }
}
