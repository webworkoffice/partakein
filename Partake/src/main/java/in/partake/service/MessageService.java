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
import in.partake.model.EventEx;
import in.partake.model.EnrollmentEx;
import in.partake.model.ParticipationList;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.Message;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventReminder;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.PartakeProperties;
import in.partake.util.PDate;
import in.partake.util.Util;

/**
 * ユーザへのメッセージングサービスを提供する。
 * 
 * @author shinyak
 */
public final class MessageService extends PartakeService {
    private static final Logger logger = Logger.getLogger(MessageService.class);
    private static MessageService instance = new MessageService();
    
    private MessageService() {
        // do nothing for now. 
    }
    
    public static MessageService get() {
        return instance;
    }
    
    public EventReminder getReminderStatus(String eventId) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            EventReminder status = getEventReminderImpl(con, eventId);
            con.commit();
            return status;
        } finally {
            con.invalidate();
        }
    }
    
    private EventReminder getEventReminderImpl(PartakeConnection con, String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        EventReminder reminder = factory.getEventReminderAccess().find(con, eventId);
        if (reminder == null) {
            return new EventReminder(eventId); 
        } else {
            return reminder;
        }
    }
    
    /**
     * send a reminder for necessary events.
     * @throws DAOException
     */
    public void sendReminders() throws DAOException {
        String topPath = PartakeProperties.get().getTopPath();      
        Date now = PDate.getCurrentDate().getDate();

        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        try {
            con.beginTransaction(); 
            
            // TODO: 開始時刻が現在時刻より後の event のみを取り出したい
            DataIterator<Event> it = factory.getEventAccess().getIterator(con);
            while (it.hasNext()) {
                Event e = it.next();
                if (e == null) { continue; }
                String eventId = e.getId();
                if (eventId == null) { continue; }
                EventEx event = getEventEx(con, eventId);
                if (event == null) { continue; }
                if (event.getBeginDate().before(now)) { continue; }
                
                // NOTE: Since the reminderStatus gotten from getEventReminderStatus is frozen,
                //       the object should be copied to use.
                EventReminder reminderStatus = new EventReminder(getEventReminderImpl(con, eventId));
                
                boolean changed = sendEventNotification(con, event, reminderStatus, topPath, now);
                if (changed) {
                    reminderStatus.setEventId(eventId);
                    factory.getEventReminderAccess().put(con, reminderStatus);
                }
            }
            
            con.commit();
        } finally {
            con.invalidate();
        }
    }
    
    private boolean sendEventNotification(PartakeConnection con, EventEx event, EventReminder reminderStatus, String topPath, Date now) throws DAOException {  
        Date beginDate = event.getBeginDate();
        Date deadline = event.getCalculatedDeadline();
        
        boolean changed = false;
        
        String shortenedURL = event.getShortenedURL();
        
        // TODO: isBeforeDeadline() とかわかりにくいな。 
        // 締め切り１日前になっても RESERVED ステータスの人がいればメッセージを送付する。
        // 次の条件でメッセージを送付する
        //  1. 現在時刻が締め切り２４時間前よりも後
        //  2. 次の条件のいずれか満たす
        //    2.1. まだメッセージが送られていない
        //    2.2. 前回送った時刻が締め切り２４時間以上前で、かつ送った時刻より１時間以上経過している。
        
        if (needsToSend(now, Util.oneDayBefore(deadline), reminderStatus.getSentDateOfBeforeDeadlineOneday())) {
            String message = "[PARTAKE] 締め切り１日前です。参加・不参加を確定してください。 " + shortenedURL + " " + event.getTitle();
            message = Util.shorten(message, 140);
            sendNotificationOnlyForReservedParticipants(con, event, message);
                                
            reminderStatus.setSentDateOfBeforeDeadlineOneday(now);
            changed = true;
        }
        
        // 締め切り１２時間前になっても RESERVED な人がいればメッセージを送付する。
        if (needsToSend(now, Util.halfDayBefore(deadline), reminderStatus.getSentDateOfBeforeDeadlineHalfday())) {
            String message = "[PARTAKE] 締め切り１２時間前です。参加・不参加を確定してください。 ３時間前までに確定されない場合、キャンセル扱いとなります。" + shortenedURL + " " + event.getTitle();
            message = Util.shorten(message, 140);
            sendNotificationOnlyForReservedParticipants(con, event, message);
                    
            reminderStatus.setSentDateOfBeforeDeadlineHalfday(now);
            changed = true;            
        }
        
        // イベント１日前で、参加が確定している人にはメッセージを送付する。
        // 参加が確定していない人には、RESERVED なメッセージが送られている。
        if (needsToSend(now, Util.oneDayBefore(beginDate), reminderStatus.getSentDateOfBeforeTheDay())) {
            String message = "[PARTAKE] イベントの１日前です。あなたの参加は確定しています。 " + shortenedURL + " " + event.getTitle();
            message = Util.shorten(message, 140);
            sendNotificationOnlyForParticipants(con, event, message);
            
            reminderStatus.setSentDateOfBeforeTheDay(now);
            changed = true;
        }
        
        return changed;
    }

    private static boolean needsToSend(Date now, Date targetDate, Date lastSent) {
        if (now.before(targetDate)) { return false; }
        if (lastSent == null) { return true; }
        if (targetDate.before(lastSent)) { return false; }
        if (now.before(new Date(lastSent.getTime() + 1000 * 3600))) { return false; }
        return true;
    }
    
    /**
     * message の内容で、仮参加者にのみメッセージを送る。
     * @param event
     * @param message
     * @throws DAOException
     */
    private void sendNotificationOnlyForReservedParticipants(PartakeConnection con, Event event, String message) throws DAOException {
        String messageId = getFactory().getDirectMessageAccess().getFreshId(con);
        Message embryo = new Message(messageId, event.getOwnerId(), message, null, new Date());
        getFactory().getDirectMessageAccess().put(con, embryo); 
        
        List<Enrollment> participations = getFactory().getEnrollmentAccess().findByEventId(con, event.getId());
        Date deadline = event.getCalculatedDeadline();
        for (Enrollment participation : participations) {
            if (!ParticipationStatus.RESERVED.equals(participation.getStatus())) { continue; }
            String envelopeId = getFactory().getEnvelopeAccess().getFreshId(con);
            Envelope envelope = new Envelope(envelopeId, participation.getUserId(), participation.getUserId(),
                    messageId, deadline, 0, null, null, DirectMessagePostingType.POSTING_TWITTER_DIRECT, new Date());
            getFactory().getEnvelopeAccess().put(con, envelope);
            logger.info("sendEnvelope : " + participation.getUserId() + " : " + embryo.getMessage());
        }
    }
    
    /**
     * message の内容で、参加確定者にのみメッセージを送る
     * @param event
     * @param message
     * @throws DAOException
     */
    private void sendNotificationOnlyForParticipants(PartakeConnection con, EventEx event, String message) throws DAOException {
        String messageId = getFactory().getDirectMessageAccess().getFreshId(con);
        Message embryo = new Message(messageId, event.getOwnerId(), message, null, new Date());
        getFactory().getDirectMessageAccess().put(con, embryo); 
        
        Date deadline = event.getCalculatedDeadline();
        
        List<EnrollmentEx> participations = getEnrollmentExs(con, event.getId()); 
        ParticipationList list = event.calculateParticipationList(participations);
        
        for (EnrollmentEx p : list.getEnrolledParticipations()) {
            if (!ParticipationStatus.ENROLLED.equals(p.getStatus())) { continue; }
            String envelopeId = getFactory().getEnvelopeAccess().getFreshId(con);
            Envelope envelope = new Envelope(envelopeId, p.getUserId(), p.getUserId(),
                    messageId, deadline, 0, null, null, DirectMessagePostingType.POSTING_TWITTER_DIRECT, new Date());
            getFactory().getEnvelopeAccess().put(con, envelope);
            logger.info("sendEnvelope : " + p.getUser().getScreenName() + " : " + embryo.getMessage());
        }
    }
    
    // TODO: 開催前のイベントだけiterateすれば充分かも
    public void sendParticipationStatusChangeNotifications() throws DAOException {
        Date now = new Date();
        
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction(); 
            DataIterator<Event> it = factory.getEventAccess().getIterator(con); 
            while (it.hasNext()) {
                Event e = it.next();
                if (e == null) { continue; }                
                String eventId = e.getId();
                if (eventId == null) { continue; }
                EventEx event = getEventEx(con, eventId); 
                if (event == null) { continue; }
                
                if (!now.before(event.getBeginDate())) { continue; }

                List<EnrollmentEx> participations = getEnrollmentExs(con, eventId); 
                ParticipationList list = event.calculateParticipationList(participations);

                String enrollingMessage = "[PARTAKE] 補欠から参加者へ繰り上がりました。 " + event.getShortenedURL() + " " + event.getTitle(); 
                String cancellingMessage = "[PARTAKE] 参加者から補欠扱い(あるいはキャンセル扱い)に変更になりました。 " + event.getShortenedURL() + " " + event.getTitle(); 
                enrollingMessage = Util.shorten(enrollingMessage, 140);
                cancellingMessage = Util.shorten(cancellingMessage, 140);

                String okMessageId = null;
                String ngMessageId = null;

                // TODO: ここのソース汚い。同一化できる。とくに、あとの２つは一緒。
                for (Enrollment p : list.getEnrolledParticipations()) {
                    // -- 参加者向
                    
                    ModificationStatus status = p.getModificationStatus();
                    if (status == null) { continue; }

                    switch (status) {
                    case CHANGED: { // 自分自身の力で変化させていた場合は status を enrolled にのみ変更して対応
                        updateLastStatus(con, eventId, p, ModificationStatus.ENROLLED);
                        break;
                    }
                    case NOT_ENROLLED: {
                        if (okMessageId == null) {
                            okMessageId = factory.getDirectMessageAccess().getFreshId(con);
                            Message okEmbryo = new Message(okMessageId, event.getOwnerId(), enrollingMessage, null, new Date()); 
                            factory.getDirectMessageAccess().put(con, okEmbryo);
                        }

                        updateLastStatus(con, eventId, p, ModificationStatus.ENROLLED);
                        String envelopeId = getFactory().getEnvelopeAccess().getFreshId(con);
                        Envelope envelope = new Envelope(envelopeId, p.getUserId(), p.getUserId(),
                                okMessageId, event.getBeginDate(), 0, null, null, DirectMessagePostingType.POSTING_TWITTER_DIRECT, new Date());
                        getFactory().getEnvelopeAccess().put(con, envelope);
                        
                        break;
                    }
                    case ENROLLED:
                        break;
                    }
                }

                for (Enrollment p : list.getSpareParticipations()) {
                    ModificationStatus status = p.getModificationStatus();
                    if (status == null) { continue; }

                    switch (status) {
                    case CHANGED: // 自分自身の力で変化させていた場合は status を not_enrolled にのみ変更して対応
                        updateLastStatus(con, eventId, p, ModificationStatus.NOT_ENROLLED);
                        break;
                    case NOT_ENROLLED:
                        break;
                    case ENROLLED:
                        if (ngMessageId == null) {
                            ngMessageId = factory.getDirectMessageAccess().getFreshId(con);
                            Message ngEmbryo = new Message(ngMessageId, event.getOwnerId(), cancellingMessage, null, new Date());
                            factory.getDirectMessageAccess().put(con, ngEmbryo); 
                        }

                        updateLastStatus(con, eventId, p, ModificationStatus.NOT_ENROLLED);
                        
                        String envelopeId = getFactory().getEnvelopeAccess().getFreshId(con);
                        Envelope envelope = new Envelope(envelopeId, p.getUserId(), p.getUserId(),
                                ngMessageId, event.getBeginDate(), 0, null, null, DirectMessagePostingType.POSTING_TWITTER_DIRECT, new Date());
                        getFactory().getEnvelopeAccess().put(con, envelope);
                        

                        break;
                    }
                }

                for (Enrollment p : list.getCancelledParticipations()) {
                    ModificationStatus status = p.getModificationStatus();
                    if (status == null) { continue; }

                    switch (status) {
                    case CHANGED: // 自分自身の力で変化させていた場合は status を not_enrolled にのみ変更して対応
                        updateLastStatus(con, eventId, p, ModificationStatus.NOT_ENROLLED);
                        break;
                    case NOT_ENROLLED:
                        break;
                    case ENROLLED:
                        if (ngMessageId == null) {
                            ngMessageId = factory.getDirectMessageAccess().getFreshId(con);
                            Message ngEmbryo = new Message(ngMessageId, event.getOwnerId(), cancellingMessage, null, new Date());
                            factory.getDirectMessageAccess().put(con, ngEmbryo); 
                        }

                        updateLastStatus(con, eventId, p, ModificationStatus.NOT_ENROLLED);
                        
                        String envelopeId = getFactory().getEnvelopeAccess().getFreshId(con);
                        Envelope envelope = new Envelope(envelopeId, p.getUserId(), p.getUserId(),
                                ngMessageId, event.getBeginDate(), 0, null, null, DirectMessagePostingType.POSTING_TWITTER_DIRECT, new Date());
                        getFactory().getEnvelopeAccess().put(con, envelope);
                        break;
                    }                   
                }
            }
            
            con.commit();
        } finally {
            con.invalidate();
        }
    }
    
    private void updateLastStatus(PartakeConnection con, String eventId, Enrollment enrollment, ModificationStatus status) throws DAOException {
        Enrollment newEnrollment = new Enrollment(enrollment);
        newEnrollment.setModificationStatus(status);
        getFactory().getEnrollmentAccess().put(con, newEnrollment);
    }
    
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
