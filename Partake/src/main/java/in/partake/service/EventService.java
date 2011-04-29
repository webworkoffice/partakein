package in.partake.service;

import in.partake.model.CommentEx;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IBinaryAccess;
import in.partake.model.dao.IEventActivityAccess;
import in.partake.model.dao.IEventRelationAccess;
import in.partake.model.dao.LuceneDao;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.BinaryData;
import in.partake.model.dto.Comment;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventActivity;
import in.partake.model.dto.EventFeedLinkage;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.Message;
import in.partake.model.dto.Questionnaire;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.pk.EnrollmentPK;
import in.partake.resource.PartakeProperties;
import in.partake.util.Util;
import in.partake.util.functional.Function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 * event にアクセスする
 * @author shinyak
 *
 * thread-safe である。
 */
public final class EventService extends PartakeService {
    private static final Logger logger = Logger.getLogger(EventService.class);

    private static EventService instance = new EventService();
    private static final String DEMO_EVENT_ID = "demo";

    public static EventService get() {
        return instance;
    }

    private EventService() {
        // do nothing for now.
    }

    // ----------------------------------------------------------------------
    // events

    /**
     * get an event from an event id.
     * @return event. null if it does not exist.
     */
    public Event getEventById(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            Event event = factory.getEventAccess().find(con, eventId);
            con.commit();

            return event;
        } finally {
            con.invalidate();
        }
    }

    /**
     * get an event-ex from an event id.
     * @param eventId
     * @return an extended event. null if it does not exist.
     * @throws DAOException
     */
    public EventEx getEventExById(String eventId) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            EventEx event = getEventEx(con, eventId);
            con.commit();

            return event;
        } finally {
            con.invalidate();
        }
    }

    /**
     * get an event from a feed id.
     * @param feedId
     * @return
     * @throws DAOException
     */
    public EventEx getEventByFeedId(String feedId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            EventFeedLinkage linkage = factory.getEventFeedAccess().find(con, feedId);
            if (linkage == null) { return null; }
            EventEx event = getEventEx(con, linkage.getEventId());
            con.commit();
            return event;
        } finally {
            con.invalidate();
        }
    }

    /**
     * 全ての event に、関数 f を適用する。f 中では ～～Service を呼ばないように注意。
     * @param f
     * @throws DAOException
     */
    public void applyForAllEvents(Function<Event, Void> f) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            DataIterator<Event> it = factory.getEventAccess().getIterator(con);
            while (it.hasNext()) {
                Event event = it.next();
                if (event == null) { continue; }
                f.apply(event);
            }
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    /**
     * feed id がついてない event に feed id をつける。
     * @throws DAOException
     */
    public void addFeedIdToAllEvents() throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            DataIterator<Event> it = factory.getEventAccess().getIterator(con);
            while (it.hasNext()) {
                Event event = it.next();
                appendFeedIfAbsent(factory, con, event.getId());
            }
            con.commit();
	    } finally {
	        con.invalidate();
	    }
	}

    /**
     * search events.
     * @param term
     * @param category
     * @param sortOrder
     * @param beforeDeadlineOnly
     * @return
     * @throws DAOException
     * @throws ParseException
     */
    public List<Event> search(String term, String category, String sortOrder, boolean beforeDeadlineOnly, int maxDocument) throws DAOException, ParseException, IllegalArgumentException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            TopDocs docs = LuceneDao.get().search(term, category, sortOrder, beforeDeadlineOnly, maxDocument);
            List<Event> events = new ArrayList<Event>();

            for (ScoreDoc doc : docs.scoreDocs) {
                Document document = LuceneDao.get().getDocument(doc.doc);
                String id = document.get("ID");
                if (id == null) {
                    logger.warn("document.get(ID) returned null. should not happen.");
                    continue;
                }

                events.add(factory.getEventAccess().find(con, id));
            }
            con.commit();

            return events;
        } finally {
            con.invalidate();
        }
    }

    /**
     * get events recently created.
     * @param num
     * @return
     * @throws DAOException
     */
    public List<Event> getRecentEvents(int num) throws DAOException {
        TopDocs docs = LuceneDao.get().getRecentDocuments(num);
        return convertToEventList(docs);
    }

    /** 近日開催されるイベントを開催時刻が近い順に返す。締切りが過ぎたイベントは含まれない。 */
    public List<Event> getUpcomingEvents(int num, String category) throws DAOException {
        if (num <= 0 || category == null) { throw new IllegalArgumentException(); }

        try {
            return EventService.get().search("", category, "beginDate", true, num);
        } catch (ParseException e) {
            // 実装ミス
            throw new RuntimeException(e);
        }
    }

    public List<Event> getRecentCategoryEvents(String category, int maxDocument) throws DAOException {
        TopDocs docs = LuceneDao.get().getRecentCategoryDocuments(category, maxDocument);
        return convertToEventList(docs);
    }

    /**
     * get events owned by the specified user.
     * TODO: user じゃなくて user id をとるようにする
     * @param owner
     * @return
     * @throws DAOException
     */
    public List<Event> getEventsOwnedBy(User owner) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            List<Event> events = factory.getEventAccess().findByOwnerId(con, owner.getId());
            con.commit();
            return events;
        } finally {
            con.invalidate();
        }
    }

    public List<Event> getEventsManagedBy(UserEx manager) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            // 1. screenName 取得
            String screenName = manager.getScreenName();
            // 2. screenName が含まれるような Event を取得
            List<Event> events = factory.getEventAccess().findByScreenName(con, screenName);

            // 3. この中に、自分が管理しているものがもしあれば取り除く。
            for (Iterator<Event> it = events.iterator(); it.hasNext(); ) {
                Event event = it.next();
                if (event == null || event.getOwnerId() == null) {
                    it.remove();
                    continue;
                }
                if (event.getOwnerId().equals(manager.getId())) {
                    it.remove();
                    continue;
                }
            }


            con.commit();
            return events;
        } finally {
            con.invalidate();
        }
    }

    /**
     * userId が管理している event で開始時刻が現在より後のものを、開始時刻順に得る。
     * @param userId
     * @return
     * @throws DAOException
     */
    public List<Event> getUnfinishedEventsOwnedBy(String userId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            Date now = new Date();
            List<Event> events = factory.getEventAccess().findByOwnerId(con, userId);
            List<Event> result = new ArrayList<Event>();
            for (Event event : events) {
                if (!event.getBeginDate().before(now)) {
                    result.add(event);
                }
            }
            con.commit();
            return result;
        } finally {
            con.invalidate();
        }
    }

    public List<Event> getUnfinishedEnrolledEvents(String userId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            List<Event> result = new ArrayList<Event>();
            Date now = new Date();

            List<Enrollment> enrollments = factory.getEnrollmentAccess().findByUserId(con, userId);
            for (Enrollment enrollment : enrollments) {
                if (enrollment == null) { continue; }
                Event e = factory.getEventAccess().find(con, enrollment.getEventId());
                if (e == null) { continue; }
                if (!e.getBeginDate().before(now)) {
                    result.add(e);
                }
            }

            con.commit();

            Collections.sort(result, Event.getComparatorBeginDateAsc());

            return result;
        } finally {
            con.invalidate();
        }
    }

    /**
     * create an event.
     * @param eventEmbryo
     * @param foreImageEmbryo
     * @param backImageEmbryo
     * @return
     * @throws DAOException
     */
    public String create(Event eventEmbryo,
            BinaryData foreImageEmbryo,
            BinaryData backImageEmbryo) throws DAOException {
        return create(eventEmbryo, foreImageEmbryo, backImageEmbryo, false);
    }

    /**
     * create an event as a demo event.
     * @param eventEmbryo
     * @param foreImageEmbryo
     * @param backImageEmbryo
     * @return
     * @throws DAOException
     */
    public String createAsDemo(Event eventEmbryo,
            BinaryData foreImageEmbryo,
            BinaryData backImageEmbryo) throws DAOException {
        return create(eventEmbryo, foreImageEmbryo, backImageEmbryo, true);
    }

    /**
     * event をデータベースに保持します。
     * @return event id
     */
    private String create(Event eventEmbryo,
            BinaryData foreImageEmbryo,
            BinaryData backImageEmbryo, boolean asDemo) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        IBinaryAccess binaryAccess = factory.getBinaryAccess();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            String foreImageId = null, backImageId = null;
            if (foreImageEmbryo != null) {
                foreImageId = binaryAccess.getFreshId(con);
                eventEmbryo.setForeImageId(foreImageId);
            }

            if (backImageEmbryo != null) {
                backImageId = binaryAccess.getFreshId(con);
                eventEmbryo.setBackImageId(backImageId);
            }


            //    		// if feed id does not exist, add one.
            //    		if (eventEmbryo.getFeedId() == null) {
            //    			eventEmbryo.setFeedId(new FeedDao().getFreshId());
            //    		}

            if (asDemo) {
                eventEmbryo.setId(DEMO_EVENT_ID);
            } else {
                String eventId = factory.getEventAccess().getFreshId(con);
                eventEmbryo.setId(eventId);
            }
            factory.getEventAccess().put(con, eventEmbryo);


            if (foreImageEmbryo != null) {
                foreImageEmbryo.setId(foreImageId);
                binaryAccess.put(con, foreImageEmbryo);
            }
            if (backImageEmbryo != null) {
                backImageEmbryo.setId(backImageId);
                binaryAccess.put(con, backImageEmbryo);
            }

            // factory.getMessageAccess().addNotification(con, eventId);

            // private でなければ Lucene にデータ挿入して検索ができるようにする
            if (!eventEmbryo.isPrivate()) {
                Document doc = makeDocument(eventEmbryo.getId(), eventEmbryo);
                LuceneDao.get().addDocument(doc);
            }

            // Feed Dao にも挿入。
            appendFeedIfAbsent(factory, con, eventEmbryo.getId());

            // Event Activity にも挿入
            {
                IEventActivityAccess eaa = factory.getEventActivityAccess();
                EventActivity activity = new EventActivity(eaa.getFreshId(con), eventEmbryo.getId(), "イベントが登録されました : " + eventEmbryo.getTitle(), eventEmbryo.getDescription(), eventEmbryo.getCreatedAt());
                eaa.put(con, activity);
            }

            // さらに、twitter bot がつぶやく (private の場合はつぶやかない)
            if (!eventEmbryo.isPrivate()) {
                tweetNewEventArrival(factory, con, eventEmbryo);
            }

            con.commit();
            return eventEmbryo.getId();
        } finally {
            con.invalidate();
        }
    }

    public void update(Event event, Event eventEmbryo,
            boolean updatesForeImage, BinaryData foreImageEmbryo,
            boolean updatesBackImage, BinaryData backImageEmbryo) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        IBinaryAccess binaryAccess = factory.getBinaryAccess();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            // まず id のみ更新
            if (updatesForeImage) {
                if (foreImageEmbryo != null) {
                    String foreImageId = event.getForeImageId() != null ? event.getForeImageId() : binaryAccess.getFreshId(con);
                    eventEmbryo.setForeImageId(foreImageId);
                } else if (event.getForeImageId() != null) {
                    eventEmbryo.setForeImageId(null);
                }
            } else {
                eventEmbryo.setForeImageId(event.getForeImageId());
            }

            if (updatesBackImage) {
                if (backImageEmbryo != null) {
                    String backImageId = event.getBackImageId() != null ? event.getBackImageId() : binaryAccess.getFreshId(con);
                    eventEmbryo.setBackImageId(backImageId);
                } else if (event.getBackImageId() != null) {
                    eventEmbryo.setBackImageId(null);
                }
            } else {
                eventEmbryo.setBackImageId(event.getBackImageId());
            }

            // master を update
            eventEmbryo.setId(event.getId());
            factory.getEventAccess().put(con, eventEmbryo);

            // その後に image たちを update
            if (updatesForeImage) {
                if (foreImageEmbryo != null) {
                    foreImageEmbryo.setId(eventEmbryo.getForeImageId());
                    binaryAccess.put(con, foreImageEmbryo);
                } else if (event.getForeImageId() != null) {
                    binaryAccess.remove(con, event.getForeImageId());
                }
            }

            if (updatesBackImage) {
                if (backImageEmbryo != null) {
                    backImageEmbryo.setId(eventEmbryo.getBackImageId());
                    binaryAccess.put(con, backImageEmbryo);
                } else if (event.getBackImageId() != null) {
                    binaryAccess.remove(con, event.getBackImageId());
                }
            }

            // Event Activity にも挿入
            {
                IEventActivityAccess eaa = factory.getEventActivityAccess();
                EventActivity activity = new EventActivity(eaa.getFreshId(con), eventEmbryo.getId(), "イベントが更新されました : " + eventEmbryo.getTitle(), eventEmbryo.getDescription(), eventEmbryo.getCreatedAt());
                eaa.put(con, activity);
            }


            // private でなければ Lucene にデータ挿入
            if (eventEmbryo.isPrivate()) {
                LuceneDao.get().removeDocument(event.getId());
            } else {
                Document doc = makeDocument(event.getId(), eventEmbryo);
                LuceneDao.get().updateDocument(doc);
            }
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    public void remove(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            factory.getEventAccess().remove(con, eventId);

            // Lucandra のデータを抜く
            LuceneDao.get().removeDocument(eventId);

            con.commit();
        } finally {
            con.invalidate();
        }
    }

    public boolean isRemoved(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        final boolean result;
        try {
            con.beginTransaction();
            result = factory.getEventAccess().isRemoved(con, eventId);
            con.commit();
        } finally {
            con.invalidate();
        }

        return result;
    }

    // TODO: this function is not thread safe?
    public void recreateEventIndex() throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            LuceneDao.get().truncate();
            DataIterator<Event> it = factory.getEventAccess().getIterator(con);
            while (it.hasNext()) {
                Event event = it.next();
                if (event == null) { continue; }
                if (event.isPrivate()) {
                    LuceneDao.get().removeDocument(event.getId());
                } else if (LuceneDao.get().hasDocument(event.getId())) {
                    Document doc = makeDocument(event.getId(), event);
                    LuceneDao.get().updateDocument(doc);
                } else {
                    Document doc = makeDocument(event.getId(), event);
                    LuceneDao.get().addDocument(doc);
                }
            }
        } finally {
            con.invalidate();
        }
    }

    // ----------------------------------------------------------------------
    // relations なのっ！

    public void setEventRelations(String eventId, List<EventRelation> relations) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            IEventRelationAccess dao = factory.getEventRelationAccess();

            // こういうふうにやると、Cassandra の場合 remove が優先されてしまう。
            //            con.beginTransaction();
            //            factory.getEventRelationAccess().removeByEventId(con, eventId);
            //            for (EventRelation er : relations) {
            //                assert (eventId.equals(er.getSrcEventId()));
            //                factory.getEventRelationAccess().put(con, er);
            //            }
            //			con.commit();

            con.beginTransaction();
            // 古いものを update/remove
            List<EventRelation> oldRelations = dao.findByEventId(con, eventId);
            for (EventRelation er : oldRelations) {
                boolean found = false;
                for (int i = 0; i < relations.size(); ++i) {
                    if (relations.get(i) == null) { continue; }
                    if (relations.get(i).getPrimaryKey().equals(er.getPrimaryKey())) {
                        found = true;
                        dao.put(con, relations.get(i));
                        relations.set(i, null);
                        break;
                    }
                }

                if (!found) {
                    dao.remove(con, er.getPrimaryKey());
                }
            }
            // 新しいものを insert
            for (int i = 0; i < relations.size(); ++i) {
                EventRelation er = relations.get(i);
                if (er == null) { continue; }
                dao.put(con, er);
            }
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    public List<EventRelation> getEventRelations(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            List<EventRelation> relations = factory.getEventRelationAccess().findByEventId(con, eventId);
            con.commit();

            return relations;
        } finally {
            con.invalidate();
        }
    }

    public List<EventRelationEx> getEventRelationsEx(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            List<EventRelationEx> relations = new ArrayList<EventRelationEx>();
            for (EventRelation relation : factory.getEventRelationAccess().findByEventId(con, eventId)) {
                if (relation == null) { continue; }
                Event event = factory.getEventAccess().find(con, relation.getDstEventId());
                if (event == null) { continue; }
                EventRelationEx relex = new EventRelationEx(relation, event);
                relex.freeze();
                relations.add(relex);
            }
            con.commit();
            return relations;
        } finally {
            con.invalidate();
        }
    }



    // ----------------------------------------------------------------------
    // enrollments

    public List<Enrollment> getParticipation(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            List<Enrollment> enrollments = factory.getEnrollmentAccess().findByEventId(con, eventId);
            con.commit();
            return enrollments;
        } finally {
            con.invalidate();
        }
    }

    /**
     * event id の enrollment list を得る。related events も考慮される。
     * @param eventId
     * @return
     * @throws DAOException
     */
    public List<EnrollmentEx> getEnrollmentEx(String eventId) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            List<EnrollmentEx> enrollments = getEnrollmentExs(con, eventId);
            con.commit();
            return enrollments;
        } finally {
            con.invalidate();
        }
    }

    /**
     * eventId に参加する userId を VIP 待遇にする。
     * @param eventId
     * @param userId
     * @return
     * @throws DAOException
     */
    public boolean makeAttendantVIP(String eventId, String userId, boolean vip) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        try {
            con.beginTransaction();
            Enrollment enrollment = factory.getEnrollmentAccess().find(con, new EnrollmentPK(userId, eventId));
            if (enrollment == null) { return false; }
            Enrollment newEnrollment = new Enrollment(enrollment);
            newEnrollment.setVIP(vip);
            factory.getEnrollmentAccess().put(con, newEnrollment);
            con.commit();
            return true;
        } finally {
            con.invalidate();
        }
    }

    public Enrollment findEnrollment(String eventId, String userId) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        try {
            return factory.getEnrollmentAccess().find(con, new EnrollmentPK(userId, eventId));
        } finally {
            con.invalidate();
        }
    }

    /**
     * eventId に参加する userId を参加していないことにする。
     * @param eventId
     * @param userId
     * @return
     * @throws DAOException
     */
    public boolean removeEnrollment(String eventId, String userId) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        try {
            con.beginTransaction();
            factory.getEnrollmentAccess().remove(con, new EnrollmentPK(userId, eventId));
            con.commit();
            return true;
        } finally {
            con.invalidate();
        }
    }

    public void udpateEnrollment(Enrollment enrollment) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        try {
            con.beginTransaction();
            factory.getEnrollmentAccess().put(con, enrollment);
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    public boolean updateAttendanceStatus(String userId, String eventId, AttendanceStatus status) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        try {
            con.beginTransaction();
            Enrollment enrollment = factory.getEnrollmentAccess().find(con, new EnrollmentPK(userId, eventId));
            if (enrollment == null) {
                con.rollback();
                return false;
            }
            Enrollment newEnrollment = new Enrollment(enrollment);
            newEnrollment.setAttendanceStatus(status);
            factory.getEnrollmentAccess().put(con, newEnrollment);
            con.commit();
            return true;
        } finally {
            con.invalidate();
        }
    }


    // ----------------------------------------------------------------------
    // Comments

    public Comment getCommentById(String commentId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            Comment comment = factory.getCommentAccess().find(con, commentId);
            con.commit();
            return comment;
        } finally {
            con.invalidate();
        }
    }

    public CommentEx getCommentExById(String commentId) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            CommentEx comment = getCommentEx(con, commentId);
            con.commit();
            return comment;
        } finally {
            con.invalidate();
        }
    }

    public void addComment(Comment embryo) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();

            embryo.setId(factory.getCommentAccess().getFreshId(con));
            factory.getCommentAccess().put(con, embryo);

            // TODO: コメント消したときにこれも消したいか？　まずいコメントが feed され続けるのは問題となりうるか？
            {
                IEventActivityAccess eaa = factory.getEventActivityAccess();
                UserEx user = getUserEx(con, embryo.getUserId());
                String title = user.getScreenName() + " さんがコメントを投稿しました";
                String content = embryo.getComment();
                eaa.put(con, new EventActivity(eaa.getFreshId(con), embryo.getEventId(), title, content, embryo.getCreatedAt()));
            }

            con.commit();
        } finally {
            con.invalidate();
        }
    }

    // TODO: うーん、comment が消される前に event が消されて、その後 comment を消そうとしたら落ちるんじゃないの？
    //
    public void removeComment(String commentId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            factory.getCommentAccess().remove(con, commentId);
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    public List<Comment> getCommentsByEvent(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            List<Comment> result = new ArrayList<Comment>();

            DataIterator<Comment> it = factory.getCommentAccess().getCommentsByEvent(con, eventId);
            if (it == null) { return result; }

            while (it.hasNext()) {
                Comment comment = it.next();
                if (comment == null) { continue; }
                result.add(comment);
            }
            con.commit();
            return result;
        } finally {
            con.invalidate();
        }
    }

    public List<CommentEx> getCommentsExByEvent(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            List<CommentEx> result = new ArrayList<CommentEx>();

            con.beginTransaction();
            DataIterator<Comment> iterator = factory.getCommentAccess().getCommentsByEvent(con, eventId);
            if (iterator == null) { return result; }

            while (iterator.hasNext()) {
                Comment comment = iterator.next();
                if (comment == null) { continue; }
                String commentId = comment.getId();
                if (commentId == null) { continue; }
                CommentEx commentEx = getCommentEx(con, commentId);
                if (commentEx == null) { continue; }
                result.add(commentEx);
            }
            con.commit();
            return result;
        } finally {
            con.invalidate();
        }
    }

    // ----------------------------------------------------------------------
    // binary data

    public BinaryData getBinaryData(String imageId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        IBinaryAccess binaryAccess = factory.getBinaryAccess();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            BinaryData data = binaryAccess.find(con, imageId);
            con.commit();
            return data;
        } finally {
            con.invalidate();
        }
    }

    /**
     * event の参加予定人数を返します。
     * @param eventId
     * @return
     * @throws DAOException
     */
    public int getNumOfEnrolledUsers(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            Event event = getEventById(eventId);
            List<Enrollment> enrollments = factory.getEnrollmentAccess().findByEventId(con, eventId);
            boolean isOver = event.isReservationTimeOver();

            int result = 0;
            for (Enrollment enrollment : enrollments) {
                switch (enrollment.getStatus()) {
                case ENROLLED:
                    ++result; break;
                case RESERVED:
                    if (!isOver) { ++result; } break;
                default:
                }
            }
            con.commit();

            return result;
        } finally {
            con.invalidate();
        }
    }

    /**
     * event の参加順位(何番目に参加したか)を返します。
     */
    public int getOrderOfEnrolledEvent(String eventId, String userId) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            List<EnrollmentEx> enrollments = getEnrollmentExs(con, eventId);
            EventEx event = getEventEx(con, eventId);
            ParticipationList list = event.calculateParticipationList(enrollments);

            int result = 0;
            for (Enrollment e : list.getEnrolledParticipations()) {
                ++result;
                if (userId.equals(e.getUserId())) { return result; }
            }
            for (Enrollment e : list.getSpareParticipations()) {
                ++result;
                if (userId.equals(e.getUserId())) { return result; }
            }
            con.commit();

            // could not found.
            logger.warn("user is not enrolled to the event.");
            return -1;
        } finally {
            con.invalidate();
        }
    }

    public void enroll(String userId, String eventId, ParticipationStatus status, String comment, boolean changesOnlyComment, boolean isReservationTimeOver) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            enrollImpl(con, userId, eventId, status, comment, changesOnlyComment, isReservationTimeOver);
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    private void enrollImpl(PartakeConnection con, String userId, String eventId, ParticipationStatus status, String comment, boolean changesOnlyComment, boolean isReservationTimeOver) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        Enrollment oldEnrollment = factory.getEnrollmentAccess().find(con, new EnrollmentPK(userId, eventId));
        Enrollment newEnrollment;
        if (oldEnrollment == null) {
            newEnrollment = new Enrollment(userId, eventId, comment, ParticipationStatus.NOT_ENROLLED, false, ModificationStatus.NOT_ENROLLED, AttendanceStatus.UNKNOWN, new Date());
        } else {
            newEnrollment = new Enrollment(oldEnrollment);
        }


        newEnrollment.setComment(comment);
        if (oldEnrollment == null) {
            newEnrollment.setStatus(status);
            newEnrollment.setModificationStatus(ModificationStatus.CHANGED);
            newEnrollment.setModifiedAt(new Date());
        } else if (changesOnlyComment || status.equals(oldEnrollment.getStatus())) {
            // 特に変更しない
        } else if (status.isEnrolled() == oldEnrollment.getStatus().isEnrolled()) {
            // 参加する / しないの状況が変更されない場合は、status のみが更新される。
            newEnrollment.setStatus(status);
            newEnrollment.setModificationStatus(ModificationStatus.CHANGED);
        } else {
            newEnrollment.setStatus(status);
            newEnrollment.setModificationStatus(ModificationStatus.CHANGED);
            newEnrollment.setModifiedAt(new Date());
        }

        //
        if (status != null) {
            IEventActivityAccess eaa = factory.getEventActivityAccess();
            UserEx user = getUserEx(con, userId);
            EventEx event = getEventEx(con, eventId);
            if (user == null) {
                return;
                //throw new IllegalArgumentException(); Hmm...
            }
            String title;
            switch (status) {
            case ENROLLED:      title = user.getScreenName() + " さんが参加しました";        break;
            case CANCELLED:     title = user.getScreenName() + " さんがを取りやめました";     break;
            case RESERVED:      title = user.getScreenName() + " さんが仮参加しました";      break;
            case NOT_ENROLLED:  title = user.getScreenName() + " さんはもう参加していません"; break;
            default:            title = user.getScreenName() + " さんが不明なステータスになっています"; break; // TODO: :-P
            }

            String content = String.format("<p>詳細は <a href=\"%s\">%s</a> をごらんください。</p>", event.getEventURL(), event.getEventURL());
            eaa.put(con, new EventActivity(eaa.getFreshId(con), eventId, title, content, new Date()));
        }

        factory.getEnrollmentAccess().put(con, newEnrollment);
    }

    // ----------------------------------------------------------------------
    // feed

    /**
     * add a feed id to the event if it does not have feed id.
     */
    public void appendFeedIfAbsent(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            appendFeedIfAbsent(factory, con, eventId);
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    private void appendFeedIfAbsent(PartakeDAOFactory factory, PartakeConnection con, String eventId) throws DAOException {
        String feedId = factory.getEventFeedAccess().findByEventId(con, eventId);
        if (feedId != null) { return; }

        feedId = factory.getEventFeedAccess().getFreshId(con);
        factory.getEventFeedAccess().put(con, new EventFeedLinkage(feedId, eventId));
    }

    public List<EventActivity> getEventActivities(String eventId, int length) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            return factory.getEventActivityAccess().findByEventId(con, eventId, length);
        } finally {
            con.invalidate();
        }

    }

    // ----------------------------------------------------------------------

    /**
     * create a lucene document from eventId and event.
     */
    private Document makeDocument(String eventId, Event eventEmbryo) {
        StringBuilder builder = new StringBuilder();
        builder.append(eventEmbryo.getTitle()).append(" ");
        builder.append(eventEmbryo.getSummary()).append(" ");
        builder.append(eventEmbryo.getAddress()).append(" ");
        builder.append(eventEmbryo.getPlace()).append(" ");
        builder.append(Util.removeTags(eventEmbryo.getDescription()));

        long beginTime = eventEmbryo.getBeginDate().getTime();
        long deadlineTime = eventEmbryo.getDeadline() != null ? eventEmbryo.getDeadline().getTime() : beginTime;
        Document doc = new Document();
        doc.add(new Field("ID", eventId, Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field("CATEGORY", eventEmbryo.getCategory(), Store.NO, Index.NOT_ANALYZED, TermVector.WITH_POSITIONS));
        doc.add(new Field("CREATED-AT", Util.getTimeString(eventEmbryo.getCreatedAt().getTime()), Store.NO, Index.NOT_ANALYZED));
        doc.add(new Field("BEGIN-TIME", Util.getTimeString(beginTime), Store.NO, Index.NOT_ANALYZED));
        doc.add(new Field("DEADLINE-TIME", Util.getTimeString(deadlineTime), Store.NO, Index.NOT_ANALYZED));
        doc.add(new Field("TITLE", eventEmbryo.getTitle(), Store.NO, Index.ANALYZED, TermVector.WITH_POSITIONS));
        doc.add(new Field("CONTENT", builder.toString(), Store.NO, Index.ANALYZED, TermVector.WITH_POSITIONS));

        return doc;
    }

    private List<Event> convertToEventList(TopDocs docs) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            List<Event> events = new ArrayList<Event>();
            for (ScoreDoc doc : docs.scoreDocs) {
                Document document = LuceneDao.get().getDocument(doc.doc);
                String id = document.get("ID");
                if (id == null) { continue; }

                events.add(factory.getEventAccess().find(con, id));
            }
            return events;
        } finally {
            con.invalidate();
        }
    }

    private void tweetNewEventArrival(PartakeDAOFactory factory, PartakeConnection con, Event event) {
        try {
            String shortenedURL = getShortenedURL(con, event);
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
            TwitterLinkage linkage = factory.getTwitterLinkageAccess().find(con, String.valueOf(twitterId));
            if (linkage == null) {
                logger.info("twitter bot does have partake user id. Login using the account once to create the user id.");
                return;
            }
            String userId = linkage.getUserId();
            if (userId == null) {
                logger.info("twitter bot does have partake user id. Login using the account once to create the user id.");
                return;
            }

            String messageId = factory.getDirectMessageAccess().getFreshId(con);
            Message embryo = new Message(messageId, userId, message, null, new Date());
            factory.getDirectMessageAccess().put(con, embryo);
            String envelopeId = factory.getEnvelopeAccess().getFreshId(con);
            Envelope envelope = new Envelope(envelopeId, userId, null, messageId, null, 0, null, null, DirectMessagePostingType.POSTING_TWITTER, new Date());
            factory.getEnvelopeAccess().put(con, envelope);

            logger.info("bot will tweet: " + message);
        } catch (Exception e) {
            logger.error("Something happened.", e);

        }
    }

    public List<Questionnaire> findQuestionnairesByEventId(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            return factory.getQuestionnaireAccess().findQuestionnairesByEventId(con, eventId);
        } finally {
            con.invalidate();
        }
    }

    public EventCount countEvents() throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        EventCount count = new EventCount();

        try {
            con.beginTransaction();
            // TODO use MapReduce for speed-up
            for (DataIterator<Event> iter = factory.getEventAccess().getIterator(con); iter.hasNext(); ) {
                Event event = iter.next();
                if (event == null) continue;
                count.numEvent++;
                if (event.isPrivate()) {
                    count.numPrivateEvent++;
                }
            }
            con.commit();
        } finally {
            con.invalidate();
        }

        return count;
    }

    public static final class EventCount {
        public int numEvent;
        public int numPrivateEvent;
    }

}
