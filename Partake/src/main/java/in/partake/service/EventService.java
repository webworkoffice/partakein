package in.partake.service;

import in.partake.functional.Function;
import in.partake.model.CommentEx;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.ParticipationEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IBinaryAccess;
import in.partake.model.dao.KeyIterator;
import in.partake.model.dao.LuceneDao;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.BinaryData;
import in.partake.model.dto.Comment;
import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.Participation;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.model.dto.auxiliary.LastParticipationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.PartakeProperties;
import in.partake.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
 * EventService 経由だと observer が使える。
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
            return factory.getEventAccess().getEventById(con, eventId);
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
            return getEventEx(con, eventId);
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
        	String eventId = factory.getFeedAccess().getEventIdByFeedId(con, feedId);
            if (eventId == null) { return null; }
            return getEventEx(con, eventId);
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
    	    KeyIterator it = factory.getEventAccess().getAllEventKeys(con);
    	    while (it.hasNext()) {
    	        String eventId = it.next();
    	        if (eventId == null) { continue; }
    	        Event event = factory.getEventAccess().getEventById(con, eventId);
    	        if (event == null) { continue; }
    	        f.apply(event);
    	    }
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
    	    KeyIterator it = factory.getEventAccess().getAllEventKeys(con);
            while (it.hasNext()) {
                String eventId = it.next();
                if (eventId == null) { continue; }
                Event event = factory.getEventAccess().getEventById(con, eventId);
                if (event == null) { continue; }
                
                appendFeedIfAbsent(factory, con, eventId);
            }
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
	public List<Event> search(String term, String category, String sortOrder, boolean beforeDeadlineOnly, int maxDocument) throws DAOException, ParseException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            TopDocs docs = LuceneDao.get().search(term, category, sortOrder, beforeDeadlineOnly, maxDocument);
            List<Event> events = new ArrayList<Event>();
            
            for (ScoreDoc doc : docs.scoreDocs) {
                Document document = LuceneDao.get().getDocument(doc.doc);
                String id = document.get("ID");
                if (id == null) {
                	logger.warn("document.get(ID) returned null. should not happen.");
                	continue;
                }
                
                events.add(factory.getEventAccess().getEventById(con, id));
            }
            
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
            return factory.getEventAccess().getEventsByOwner(con, owner.getId());
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
            Date now = new Date();
            List<Event> events = factory.getEventAccess().getEventsByOwner(con, userId);
            List<Event> result = new ArrayList<Event>();
            for (Event event : events) {
                if (!event.getBeginDate().before(now)) {
                    result.add(event);
                }
            }
            return result;
        } finally {
            con.invalidate();
        }	    
	}
	
	public List<Event> getUnfinishedEnrolledEvents(String userId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            List<Event> result = new ArrayList<Event>();
            Date now = new Date();
            
            DataIterator<Event> it = factory.getEnrollmentAccess().getEnrolledEvents(con, userId);
            while (it.hasNext()) {
                Event e = it.next();
                if (e == null) { continue; }
                if (!e.getBeginDate().before(now)) {
                    result.add(e);
                }
            }
            
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
    		factory.getEventAccess().addEvent(con, eventEmbryo);
    		
    		
    		if (foreImageEmbryo != null) {
    		    foreImageEmbryo.setId(foreImageId);
    		    binaryAccess.addBinary(con, foreImageEmbryo);			
    		}
    		if (backImageEmbryo != null) {
    		    backImageEmbryo.setId(backImageId);
    		    binaryAccess.addBinary(con, backImageEmbryo);			
    		}
            
    		// factory.getMessageAccess().addNotification(con, eventId);

        	// private でなければ Lucene にデータ挿入して検索ができるようにする
        	if (!eventEmbryo.isPrivate()) {
    	    	Document doc = makeDocument(eventEmbryo.getId(), eventEmbryo);
    	    	LuceneDao.get().addDocument(doc);
        	}
        	
        	// Feed Dao にも挿入。
        	appendFeedIfAbsent(factory, con, eventEmbryo.getId());
        	
        	
        	// さらに、twitter bot がつぶやく (private の場合はつぶやかない)
        	if (!eventEmbryo.isPrivate()) {
        	    tweetNewEventArrival(factory, con, eventEmbryo);
        	}
        	
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
    		factory.getEventAccess().updateEvent(con, event, eventEmbryo);
    		// event の revision を update
    		factory.getEventAccess().updateEventRevision(con, event.getId());
    		
    		// その後に image たちを update
    		if (updatesForeImage) {
    			if (foreImageEmbryo != null) {
    			    foreImageEmbryo.setId(eventEmbryo.getForeImageId());
    				binaryAccess.addBinary(con, foreImageEmbryo);
    			} else if (event.getForeImageId() != null) {    			    
    				binaryAccess.removeBinary(con, event.getForeImageId());
    			}
    		}
    		
    		if (updatesBackImage) {
    			if (backImageEmbryo != null) {
    			    backImageEmbryo.setId(eventEmbryo.getBackImageId());
    				binaryAccess.addBinary(con, backImageEmbryo);
    			} else if (event.getBackImageId() != null) {
    				binaryAccess.removeBinary(con, event.getBackImageId());
    			}		
    		}
    		
    		// private でなければ Lucene にデータ挿入
    		if (eventEmbryo.isPrivate()) {
    			LuceneDao.get().removeDocument(event.getId());
    		} else {
    			Document doc = makeDocument(event.getId(), eventEmbryo);
    			LuceneDao.get().updateDocument(doc);
    		}
        } finally {
            con.invalidate();
        }
	}

	public void remove(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();       
        PartakeConnection con = getPool().getConnection();
        try {
            factory.getEventAccess().removeEvent(con, eventId);

            // Lucandra のデータを抜く
            LuceneDao.get().removeDocument(eventId);            
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
			factory.getEventRelationAccess().setEventRelations(con, eventId, relations);
		} finally {
			con.invalidate();
		}
	}
	
	public List<EventRelation> getEventRelations(String eventId) throws DAOException {
		PartakeDAOFactory factory = getFactory();
		PartakeConnection con = getPool().getConnection();
		try {
			return getEventRelations(factory, con, eventId);
		} finally {
			con.invalidate();
		}		
	}
	
	public List<EventRelationEx> getEventRelationsEx(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            List<EventRelationEx> relations = new ArrayList<EventRelationEx>();
            for (EventRelation relation : getEventRelations(factory, con, eventId)) {
                if (relation == null) { continue; }
                EventEx event = getEventEx(con, relation.getEventId());
                if (event == null) { continue; }
                EventRelationEx relex = new EventRelationEx(relation, event);
                relex.freeze();
                relations.add(relex);
            }
            
            return relations;
        } finally {
            con.invalidate();
        }
	}
	
	public List<EventRelation> getEventRelations(PartakeDAOFactory factory, PartakeConnection con, String eventId) throws DAOException {
		return factory.getEventRelationAccess().getEventRelations(con, eventId);
	}

    // ----------------------------------------------------------------------
    // participations
	
	public List<Participation> getParticipation(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            return factory.getEnrollmentAccess().getParticipation(con, eventId);
        } finally {
            con.invalidate();
        }
	}
	
	/**
	 * event id の participation list を得る。related events も考慮される。
	 * @param eventId
	 * @return
	 * @throws DAOException
	 */
	public List<ParticipationEx> getParticipationEx(String eventId) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        try {
            return getParticipationsEx(con, eventId);
        } finally {
            con.invalidate();
        }	    
	}
	
	public void setLastStatus(String eventId, Participation p, LastParticipationStatus lastStatus) throws DAOException {	    
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            factory.getEnrollmentAccess().setLastStatus(con, eventId, p, lastStatus);
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
	    	return factory.getCommentAccess().getComment(con, commentId);
	    } finally {
	        con.invalidate();
	    }
	}
	
	public CommentEx getCommentExById(String commentId) throws DAOException {
	    PartakeConnection con = getPool().getConnection();
	    try {
	    	return getCommentEx(con, commentId);
	    } finally {
	        con.invalidate();
	    }		
	}
	
	public void addComment(Comment embryo) throws DAOException {
	    PartakeDAOFactory factory = getFactory(); 
	    PartakeConnection con = getPool().getConnection();
	    try {
    	    embryo.setId(factory.getCommentAccess().getFreshId(con));
    	    factory.getCommentAccess().addComment(con, embryo);
	    } finally {
	        con.invalidate();
	    }
	}
	
	// TODO: うーん、comment が消される前に event が消されて、その後 comment を消そうとしたら落ちるんじゃないの？
	public void removeComment(String commentId) throws DAOException {
        PartakeDAOFactory factory = getFactory(); 
        PartakeConnection con = getPool().getConnection();
	    try {
	    	factory.getCommentAccess().removeComment(con, commentId);
	    } finally {
	        con.invalidate();
	    }
	}
	
    public List<Comment> getCommentsByEvent(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory(); 
        PartakeConnection con = getPool().getConnection();
        try {
            List<Comment> result = new ArrayList<Comment>();
            
            DataIterator<Comment> it = factory.getCommentAccess().getCommentsByEvent(con, eventId);
            if (it == null) { return result; }
            
            while (it.hasNext()) {
                Comment comment = it.next();
                result.add(comment);
            }
            
            return result;
        } finally {
            con.invalidate();
        }
    }
    
    public List<CommentEx> getCommentsExByEvent(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory(); 
        PartakeConnection con = getPool().getConnection();
        try {
            DataIterator<Comment> iterator = factory.getCommentAccess().getCommentsByEvent(con, eventId);
            
            List<CommentEx> result = new ArrayList<CommentEx>();
            while (iterator.hasNext()) {
                Comment comment = iterator.next();
                String commentId = comment.getComment();
                if (commentId == null) { continue; }
                CommentEx commentEx = getCommentEx(con, commentId);
                if (commentEx == null) { continue; }
                result.add(commentEx);
            }
            
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
            return binaryAccess.getBinary(con, imageId);
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
            Event event = factory.getEventAccess().getEventById(con, eventId);           
            return factory.getEnrollmentAccess().getNumOfParticipants(con, eventId, event.isReservationTimeOver());
        } finally {
            con.invalidate();
        }           
    }
    
    /**
     * event の参加順位(何番目に参加したか)を返します。
     */
    public int getOrderOfEnrolledEvent(String eventId, String userId) throws DAOException {
        PartakeDAOFactory factory = getFactory();       
        PartakeConnection con = getPool().getConnection();
        try {
            Event event = factory.getEventAccess().getEventById(con, eventId);           
            return factory.getEnrollmentAccess().getOrderOfEnrolledEvent(con, eventId, userId, event.isReservationTimeOver());
        } finally {
            con.invalidate();
        }   
    }
    
    // TODO: 書き直せ
    public void enroll(User user, Event event, ParticipationStatus status, String comment, 
                    boolean changesOnlyComment, boolean forceChangeModifiedAt) throws DAOException {
        PartakeDAOFactory factory = getFactory();       
        PartakeConnection con = getPool().getConnection();
        try {
            factory.getEnrollmentAccess().enroll(con, user, event, status, comment, changesOnlyComment, forceChangeModifiedAt);
        } finally {
            con.invalidate();
        }        
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
            appendFeedIfAbsent(factory, con, eventId);
        } finally {
            con.invalidate();
        } 
    }
    
    private void appendFeedIfAbsent(PartakeDAOFactory factory, PartakeConnection con, String eventId) throws DAOException {
        String feedId = factory.getFeedAccess().getFeedIdByEventId(con, eventId);
        if (feedId != null) { return; }
        
        feedId = factory.getFeedAccess().getFreshId(con);
        factory.getFeedAccess().addFeedId(con, feedId, eventId);
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

                events.add(factory.getEventAccess().getEventById(con, id));
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
            TwitterLinkage linkage = factory.getTwitterLinkageAccess().getTwitterLinkageById(con, twitterId);
            if (linkage == null) {
                logger.info("twitter bot does have partake user id. Login using the account once to create the user id.");
                return;
            }
            String userId = linkage.getUserId();
            if (userId == null) {
                logger.info("twitter bot does have partake user id. Login using the account once to create the user id.");
                return; 
            }
            
            DirectMessage embryo = new DirectMessage(userId, message);
            String messageId = factory.getDirectMessageAccess().getFreshId(con);
            factory.getDirectMessageAccess().addMessage(con, messageId, embryo);
            factory.getDirectMessageAccess().sendEnvelope(con, messageId, userId, null, null, DirectMessagePostingType.POSTING_TWITTER);
            logger.info("bot will tweet: " + message);
    	} catch (Exception e) {
    	    logger.error("Something happened.", e);
    	    
    	}
	}
}
