package in.partake.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;

import in.partake.model.DirectMessageEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.Message;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;

// TOOD: This class will be merged into MessageService later.
public class DirectMessageService extends PartakeService {
    private static DirectMessageService instance = new DirectMessageService();
    private static final Logger logger = Logger.getLogger(DirectMessageService.class);
    
    DirectMessageService() {
        // do nothing for now.
    }
    
    public static DirectMessageService get() {
        return instance;
    }
    
    // ----------------------------------------------------------------------

    public Message getMessageById(String messageId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            Message message = factory.getDirectMessageAccess().find(con, messageId);
            con.commit();
            
            return message;
        } finally {
            con.invalidate();
        }
    }
    
    /**
     * message を DB に格納する。
     * DB に格納するだけで送られない。
     * @param embryo
     * @return message の ID を返す
     * @throws DAOException
     */
    public String addMessage(String userId, String message, String eventId, boolean isUserMessage) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();

        
        try {
            con.beginTransaction();
            String id = factory.getDirectMessageAccess().getFreshId(con);
            Message embryo = new Message(id, userId, message, isUserMessage ? eventId : null, new Date()); 
            factory.getDirectMessageAccess().put(con, embryo);
            con.commit();
            
            return id;
        } finally {
            con.invalidate();
        }
    }
    
    /**
     * ある event で管理者がユーザーに送ったメッセージを送った順に取得する。
     * @param eventId
     * @return
     * @throws DAOException
     */
    public List<DirectMessageEx> getUserMessagesByEventId(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
    	PartakeConnection con = getPool().getConnection();
    	
    	try {
    	    con.beginTransaction();
    	    
	        List<DirectMessageEx> messages = new ArrayList<DirectMessageEx>();
	        DataIterator<Message> it = factory.getDirectMessageAccess().findByEventId(con, eventId);

	        while (it.hasNext()) {
	        	Message message = it.next();
	        	messages.add(new DirectMessageEx(message, getUserEx(con, message.getUserId())));
	        }
	        
	        con.commit();
	                
	        return messages;
    	} finally {
    		con.invalidate();
    	}
    }
    
    /**
     * 管理者が送ったユーザーに送ったメッセージを最大 maxMessage 個取得する。
     * @param eventId
     * @param maxMessage
     * @return
     * @throws DAOException
     */
    public List<Message> getRecentUserMessage(String eventId, int maxMessage) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            List<Message> messages = new ArrayList<Message>();
            DataIterator<Message> it = factory.getDirectMessageAccess().findByEventId(con, eventId);
            
            for (int i = 0; i < maxMessage; ++i) {
                if (!it.hasNext()) { break; }
                messages.add(it.next());
            }
            
            con.commit();
                    
            return messages;
        } finally {
            con.invalidate();
        }
    }
    
    /**
     * message を tweet する。DM として tweet するのではない。
     * @param user
     * @param messageStr
     * @throws DAOException
     */
    public void tweetMessage(User user, String messageStr) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();

        try {
            con.beginTransaction();
            String messageId = factory.getDirectMessageAccess().getFreshId(con);
            Message embryo = new Message(messageId, user.getId(), messageStr, null, new Date());
            
            factory.getDirectMessageAccess().put(con, embryo);
            
            String envelopeId = factory.getEnvelopeAccess().getFreshId(con);
            Envelope envelope = new Envelope(envelopeId, user.getId(), null, messageId, null, 0, null, null, DirectMessagePostingType.POSTING_TWITTER, new Date());
            factory.getEnvelopeAccess().put(con, envelope);
            
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    /**
     * message を、実際に送信する (ための queue に挿入する)。
     * 
     * @param messageId
     * @param senderId
     * @param receiverId
     * @param deadline
     * @param postingType
     * @throws DAOException
     */
    public void sendEnvelope(String messageId, String senderId, String receiverId, Date deadline, DirectMessagePostingType postingType) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();

        try {
            con.beginTransaction();
            String envelopeId = factory.getEnvelopeAccess().getFreshId(con);
            Envelope envelope = new Envelope(envelopeId, senderId, receiverId, messageId, deadline, 0, null, null, postingType, new Date());
            factory.getEnvelopeAccess().put(con, envelope);
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    /**
     * queue から message を取得し、実際に送信する。
     * @throws DAOException
     */
    public void sendEnvelopes() throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            DataIterator<Envelope> it = factory.getEnvelopeAccess().getIterator(con);             
            while (it.hasNext()) {
                Envelope envelope = it.next();
                if (envelope == null) { it.remove(); continue; }
    
                logger.debug("run : Try to send... " + envelope.getEnvelopeId());
                
                // deadline を超えていれば送らない。
                Date now = new Date();
                if (envelope.getDeadline() != null && envelope.getDeadline().before(now)) {
                    logger.warn("run : envelope id " + envelope.getEnvelopeId() + " could not be sent : Time out.");
                    it.remove();
                    continue;
                }
                
                // tryAfter 前であれば送らない。
                if (envelope.getTryAfter() != null && !envelope.getTryAfter().before(now)) {
                    logger.debug("run : envelope id " + envelope.getEnvelopeId() + " should be sent after " + envelope.getTryAfter());
                    continue;
                }
    
                
                switch (envelope.getPostingType()) {
                case POSTING_TWITTER_DIRECT:
                    if (sendDirectMessage(con, it, envelope)) { it.remove(); }
                    break;
                case POSTING_TWITTER:
                    if (sendTwitterMessage(con, it, envelope)) { it.remove(); }
                    break;
                }
            }
            con.commit();
        } finally {
            con.invalidate();
        }
    }
    
    // ----------------------------------------------------------------------
    
    
    private boolean sendTwitterMessage(PartakeConnection con, DataIterator<Envelope> it, Envelope envelope) throws DAOException {
        String senderId = envelope.getSenderId();
        assert (envelope.getReceiverId() == null);
        if (senderId == null) {
            logger.warn("sendTwitterMessage : senderId is null.");
            return true;
        }
        
        UserEx sender = getUserEx(con, senderId);
        if (sender == null) {
            logger.warn("sendTwitterMessage : sender is null.");
            return true;
        }
        
        AccessToken accessToken = new AccessToken(sender.getTwitterLinkage().getAccessToken(), sender.getTwitterLinkage().getAccessTokenSecret());
        Twitter twitter = new TwitterFactory().getInstance(accessToken);
        
        try {
            Message message = getFactory().getDirectMessageAccess().find(con, envelope.getMessageId());             
            twitter.updateStatus(message.getMessage());
            return true;
        } catch (TwitterException e) {
            if (e.isCausedByNetworkIssue()) {
                logger.warn("Twitter Unreachable?", e);
                envelope.updateForSendingFailure();
                it.update(envelope);
                return false;
            } else if (e.exceededRateLimitation()) {
                envelope.updateForSendingFailure();
                int retryAfterInSeconds = e.getRetryAfter();
                envelope.setTryAfter(new Date(new Date().getTime() + retryAfterInSeconds * 1000));
                it.update(envelope);
                return false;
            } else {
                logger.warn("sendTwitterMessage : Unknown Error : " + envelope.getEnvelopeId() + " was failed to deliver.", e);
                return true;
            }
        }
    }
    
    /**
     * Envelope を送信する。true を返すと送ることができた / もうこれ以上送ってはいけないという意味になる。
     * @param envelope
     * @return
     */
    private boolean sendDirectMessage(PartakeConnection con, DataIterator<Envelope> it, Envelope envelope) throws DAOException {        
        String receiverId = envelope.getReceiverId();

        // twitter message を受け取らない設定になっていれば送らない。
        UserPreference pref = getFactory().getUserPreferenceAccess().find(con, receiverId);
        if (pref == null) {
            pref = UserPreference.getDefaultPreference(receiverId);
        }
        
        if (!pref.isReceivingTwitterMessage()) { return true; }

        UserEx user = getUserEx(con, receiverId);
        if (user == null) { return true; }
        TwitterLinkage twitterLinkage = user.getTwitterLinkage();
        
        if (twitterLinkage.getAccessToken() == null || twitterLinkage.getAccessTokenSecret() == null) {
            logger.warn("sendDirectMessage : envelope id " + envelope.getEnvelopeId() + " could not be sent : No access token");
            return true;
        }
        
        AccessToken accessToken = new AccessToken(twitterLinkage.getAccessToken(), twitterLinkage.getAccessTokenSecret());
        Twitter twitter = new TwitterFactory().getInstance(accessToken);
        if (twitter == null) { return true; }

        try {
            Message message = getFactory().getDirectMessageAccess().find(con, envelope.getMessageId());
            int twitterId = Integer.parseInt(user.getTwitterId());
            twitter.sendDirectMessage(twitterId, message.getMessage());
                            
            logger.info("sendDirectMessage : direct message has been sent to " + twitterLinkage.getScreenName());
            return true;
        } catch (NumberFormatException e) {
            logger.error("twitterId has not a number.", e);
            return true;
        } catch (TwitterException e) {
            if (e.isCausedByNetworkIssue()) {
                logger.warn("sendDirectMessage : Twitter Unreachable?", e);
                envelope.updateForSendingFailure();
                it.update(envelope);
                return false;
            } else if (e.exceededRateLimitation()) {
                envelope.updateForSendingFailure();
                int retryAfterInSeconds = e.getRetryAfter();
                envelope.setTryAfter(new Date(new Date().getTime() + retryAfterInSeconds * 1000));
                it.update(envelope);
                return false;
            } else {
                envelope.updateForSendingFailure();
                logger.warn("sendDirectMessage : Unknown Error : " + envelope.getEnvelopeId() + " was failed to deliver.", e);
                return true;
            }
        }
    }
}
