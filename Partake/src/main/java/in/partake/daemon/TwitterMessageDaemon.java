package in.partake.daemon;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.DirectMessageEnvelope;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.model.dto.UserPreference;
import in.partake.service.DirectMessageService;
import in.partake.service.UserService;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;


class DirectMessageSendingTask extends TimerTask {
	private boolean working = false;
    private static final Logger logger = Logger.getLogger(DirectMessageSendingTask.class);
    
    @Override
    public void run() {
    	logger.info("DirectMessageSendingTask START");
    	working = true;
        try {
            DataIterator<DirectMessageEnvelope> it = DirectMessageService.get().getEnvelopeIterator();             
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
        } catch (DAOException e) {
            logger.warn("run() failed.", e);
        }        
        
        logger.info("DirectMessageSendingTask END");
    }
    
    private boolean sendTwitterMessage(DataIterator<DirectMessageEnvelope> it, DirectMessageEnvelope envelope) throws DAOException {
        String senderId = envelope.getSenderId();
        assert (envelope.getReceiverId() == null);
        if (senderId == null) {
            logger.warn("sendTwitterMessage : senderId is null.");
            return true;
        }
        
        User sender = UserService.get().getUserById(senderId);
        if (sender == null) {
            logger.warn("sendTwitterMessage : sender is null.");
            return true;
        }
        
        UserEx partakeSender = UserService.get().getPartakeUserByUser(sender);
        AccessToken accessToken = new AccessToken(partakeSender.getTwitterLinkage().getAccessToken(), partakeSender.getTwitterLinkage().getAccessTokenSecret());
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

        User user = UserService.get().getUserById(receiverId);      
        UserEx partakeUser = UserService.get().getPartakeUserByUser(user);
        TwitterLinkage twitterLinkage = partakeUser.getTwitterLinkage();
        
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

public final class TwitterMessageDaemon {
	// private static final int TIMER_INTERVAL_IN_MILLIS = 2 * 60 * 1000; // ２分
    // private static final int TIMER_INTERVAL_IN_MILLIS = 10000; // １０秒
    private static final int TIMER_INTERVAL_IN_MILLIS = 30000; // ３０秒
	
	private static TwitterMessageDaemon instance = new TwitterMessageDaemon();
	private Timer timerForDirectMessage;
		
	public static TwitterMessageDaemon getInstance() {
		return instance;
	}
	
	private TwitterMessageDaemon() {
		timerForDirectMessage = new Timer();
	}
	
	public void schedule() {
		timerForDirectMessage.schedule(new DirectMessageSendingTask(), 0, TIMER_INTERVAL_IN_MILLIS);
	}
	
	public void cancel() {
		timerForDirectMessage.cancel();
	}
}
