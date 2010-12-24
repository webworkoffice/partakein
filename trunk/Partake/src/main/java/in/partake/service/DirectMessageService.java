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
import in.partake.model.dao.PartakeModelFactory;
import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.DirectMessageEnvelope;
import in.partake.model.dto.DirectMessagePostingType;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.model.dto.UserPreference;

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

    public DirectMessage getMessageById(String messageId) throws DAOException {
        PartakeModelFactory factory = getFactory();
        PartakeConnection con = factory.getConnection();
        try {
            return factory.getDirectMessageAccess().getDirectMessageById(con, messageId);
        } finally {
            con.invalidate();
        }
    }
    
    /**
     * message を DB に格納する。
     * DB に格納するだけ。
     * @param embryo
     * @param isUserMessage ユーザーからのメッセージであれば true / system からのメッセージであれば false
     * @return message の ID を返す
     * @throws DAOException
     */
    public String addMessage(DirectMessage embryo, boolean isUserMessage) throws DAOException {
        PartakeModelFactory factory = getFactory();
        PartakeConnection con = factory.getConnection();

        try {
            String messageId = factory.getDirectMessageAccess().getFreshId(con);
            factory.getDirectMessageAccess().addMessage(con, messageId, embryo); 
            if (isUserMessage) {
                factory.getDirectMessageAccess().addUserMessage(con, messageId, embryo.getEventId());
            }

            return messageId;
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
    	PartakeModelFactory factory = getFactory();
    	PartakeConnection con = factory.getConnection();
    	
    	try {
	        List<DirectMessageEx> messages = new ArrayList<DirectMessageEx>();
	        DataIterator<DirectMessage> it = factory.getDirectMessageAccess().getUserMessageIterator(factory, eventId);

	        while (it.hasNext()) {
	        	DirectMessage message = it.next();
	        	messages.add(new DirectMessageEx(message, getUserEx(con, message.getUserId())));
	        }
	                
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
    public List<DirectMessage> getRecentUserMessage(String eventId, int maxMessage) throws DAOException {
        PartakeModelFactory factory = getFactory();

        List<DirectMessage> messages = new ArrayList<DirectMessage>();
        DataIterator<DirectMessage> it = factory.getDirectMessageAccess().getUserMessageIterator(factory, eventId);
        
        for (int i = 0; i < maxMessage; ++i) {
            if (!it.hasNext()) { break; }
            messages.add(it.next());
        }
                
        return messages;
    }
    
    /**
     * message を tweet する。DM として tweet するのではない。
     * @param user
     * @param messageStr
     * @throws DAOException
     */
    public void tweetMessage(User user, String messageStr) throws DAOException {
        PartakeModelFactory factory = getFactory();
        PartakeConnection con = factory.getConnection();

        try {
            DirectMessage embryo = new DirectMessage(user.getId(), messageStr);
            String messageId = factory.getDirectMessageAccess().getFreshId(con);
            factory.getDirectMessageAccess().addMessage(con, messageId, embryo);
            factory.getDirectMessageAccess().sendEnvelope(con, messageId, user.getId(), null, null, DirectMessagePostingType.POSTING_TWITTER);
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
        PartakeModelFactory factory = getFactory();
        PartakeConnection con = factory.getConnection();

        try {
            factory.getDirectMessageAccess().sendEnvelope(con, messageId, senderId, receiverId, deadline, postingType);
        } finally {
            con.invalidate();
        }
    }

    /**
     * queue から message を取得し、実際に送信する。
     * @throws DAOException
     */
    public void sendEnvelopes() throws DAOException {
        PartakeModelFactory factory = getFactory();
        DataIterator<DirectMessageEnvelope> it = factory.getDirectMessageAccess().getEnvelopeIterator(factory);             
        while (it.hasNext()) {
            DirectMessageEnvelope envelope = it.next();
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
                if (sendDirectMessage(it, envelope)) { it.remove(); }
                break;
            case POSTING_TWITTER:
                if (sendTwitterMessage(it, envelope)) { it.remove(); }
                break;
            }
        }        
    }
    
    // ----------------------------------------------------------------------
    
    
    private boolean sendTwitterMessage(DataIterator<DirectMessageEnvelope> it, DirectMessageEnvelope envelope) throws DAOException {
        String senderId = envelope.getSenderId();
        assert (envelope.getReceiverId() == null);
        if (senderId == null) {
            logger.warn("sendTwitterMessage : senderId is null.");
            return true;
        }
        
        UserEx sender = UserService.get().getUserExById(senderId);
        if (sender == null) {
            logger.warn("sendTwitterMessage : sender is null.");
            return true;
        }
        
        AccessToken accessToken = new AccessToken(sender.getTwitterLinkage().getAccessToken(), sender.getTwitterLinkage().getAccessTokenSecret());
        Twitter twitter = new TwitterFactory().getOAuthAuthorizedInstance(accessToken);
        
        try {
            DirectMessage message = DirectMessageService.get().getMessageById(envelope.getMessageId());
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
                logger.warn("sendTwitterMessage : Unknown Error", e);
                return true;
            }
        }
    }
    
    /**
     * Envelope を送信する。true を返すと送ることができた / もうこれ以上送ってはいけないという意味になる。
     * @param envelope
     * @return
     */
    private boolean sendDirectMessage(DataIterator<DirectMessageEnvelope> it, DirectMessageEnvelope envelope) throws DAOException {        
        String receiverId = envelope.getReceiverId();

        // twitter message を受け取らない設定になっていれば送らない。
        UserPreference pref = UserService.get().getUserPreference(receiverId);
        if (!pref.isReceivingTwitterMessage()) { return true; }

        UserEx user = UserService.get().getUserExById(receiverId);
        TwitterLinkage twitterLinkage = user.getTwitterLinkage();
        
        if (twitterLinkage.getAccessToken() == null || twitterLinkage.getAccessTokenSecret() == null) {
            logger.warn("sendDirectMessage : envelope id " + envelope.getEnvelopeId() + " could not be sent : No access token");
            return true;
        }
        
        AccessToken accessToken = new AccessToken(twitterLinkage.getAccessToken(), twitterLinkage.getAccessTokenSecret());
        Twitter twitter = new TwitterFactory().getOAuthAuthorizedInstance(accessToken);
        if (twitter == null) { return true; }

        try {
            DirectMessage message = DirectMessageService.get().getMessageById(envelope.getMessageId());
            twitter.sendDirectMessage(user.getTwitterId(), message.getMessage());
            logger.info("sendDirectMessage : direct message has been sent to " + twitterLinkage.getScreenName());
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
                logger.warn("sendDirectMessage : Unknown Error", e);
                return true;
            }
        }
    }
}
