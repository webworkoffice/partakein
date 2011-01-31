package in.partake.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import in.partake.model.EventEx;
import in.partake.model.ParticipationEx;
import in.partake.model.ParticipationList;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.KeyIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventReminderStatus;
import in.partake.model.dto.Participation;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.model.dto.auxiliary.LastParticipationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.PartakeProperties;
import in.partake.util.PDate;
import in.partake.util.Util;

public final class MessageService extends PartakeService {
    private static final Logger logger = Logger.getLogger(MessageService.class);
    private static MessageService instance = new MessageService();
    
    private MessageService() {
        // do nothing for now. 
    }
    
    public static MessageService get() {
        return instance;
    }
    
    public EventReminderStatus gerReminderStatus(String eventId) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        try {
            return factory.getDirectMessageAccess().getEventReminderStatus(con, eventId);
        } finally {
            con.invalidate();
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
            // TODO: 開始時刻が現在時刻より後の event のみを取り出したい 
            KeyIterator it = factory.getEventAccess().getAllEventKeys(con);
            while (it.hasNext()) {
                String eventId = it.next();
                if (eventId == null) { continue; }
                EventEx event = getEventEx(con, eventId);
                if (event == null) { continue; }
                if (event.getBeginDate().before(now)) { continue; }
                
                // NOTE: Since the reminderStatus gotten from getEventReminderStatus is frozen,
                //       the object should be copied to use. 
                EventReminderStatus reminderStatus = 
                    new EventReminderStatus(factory.getDirectMessageAccess().getEventReminderStatus(con, eventId));
                
                boolean changed = sendEventNotification(con, event, reminderStatus, topPath, now);
                if (changed) {
                    factory.getDirectMessageAccess().updateEventReminderStatus(con, eventId, reminderStatus);
                }
            }
        } finally {
            con.invalidate();
        }
    }
    
    private boolean sendEventNotification(PartakeConnection con, EventEx event, EventReminderStatus reminderStatus, String topPath, Date now) throws DAOException {  
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
        DirectMessage embryo = new DirectMessage(event.getOwnerId(), message);
        
        String messageId = getFactory().getDirectMessageAccess().getFreshId(con);
        getFactory().getDirectMessageAccess().addMessage(con, messageId, embryo); 
        
        List<Participation> participations = getFactory().getEnrollmentAccess().getParticipation(con, event.getId()); 
        Date deadline = event.getCalculatedDeadline();
        for (Participation participation : participations) {
            if (!ParticipationStatus.RESERVED.equals(participation.getStatus())) { continue; }
            getFactory().getDirectMessageAccess().sendEnvelope(con,
                            messageId, participation.getUserId(), participation.getUserId(), deadline,                                
                            DirectMessagePostingType.POSTING_TWITTER_DIRECT);
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
        DirectMessage embryo = new DirectMessage(event.getOwnerId(), message);
        
        String messageId = getFactory().getDirectMessageAccess().getFreshId(con);
        getFactory().getDirectMessageAccess().addMessage(con, messageId, embryo); 
        
        Date deadline = event.getCalculatedDeadline();
        
        List<ParticipationEx> participations = getParticipationsEx(con, event.getId()); 
        ParticipationList list = event.calculateParticipationList(participations);
        
        for (ParticipationEx p : list.getEnrolledParticipations()) {
            if (!ParticipationStatus.ENROLLED.equals(p.getStatus())) { continue; }
            getFactory().getDirectMessageAccess().sendEnvelope(con,    
                    messageId, p.getUserId(), p.getUserId(), deadline,
                    DirectMessagePostingType.POSTING_TWITTER_DIRECT);
            logger.info("sendEnvelope : " + p.getUser().getScreenName() + " : " + embryo.getMessage());
        }
    }
    
    // TODO: 開催前のイベントだけiterateすれば充分かも
    public void sendParticipationStatusChangeNotifications() throws DAOException {
        Date now = new Date();
        
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            KeyIterator it = factory.getEventAccess().getAllEventKeys(con);
            while (it.hasNext()) {
                String eventId = it.next();
                if (eventId == null) { continue; }
                EventEx event = getEventEx(con, eventId); 
                if (event == null) { continue; }
                
                if (!now.before(event.getBeginDate())) { continue; }

                List<ParticipationEx> participations = getParticipationsEx(con, eventId); 
                ParticipationList list = event.calculateParticipationList(participations);

                String enrollingMessage = "[PARTAKE] 補欠から参加者へ繰り上がりました。 " + event.getShortenedURL() + " " + event.getTitle(); 
                String cancellingMessage = "[PARTAKE] 参加者から補欠扱い(あるいはキャンセル扱い)に変更になりました。 " + event.getShortenedURL() + " " + event.getTitle(); 
                enrollingMessage = Util.shorten(enrollingMessage, 140);
                cancellingMessage = Util.shorten(cancellingMessage, 140);

                String okMessageId = null;
                String ngMessageId = null;

                // TODO: ここのソース汚い。同一化できる。とくに、あとの２つは一緒。
                for (Participation p : list.getEnrolledParticipations()) {
                    // -- 参加者向
                    
                    LastParticipationStatus status = p.getLastStatus();
                    if (status == null) { continue; }

                    switch (status) {
                    case CHANGED: // 自分自身の力で変化させていた場合は status を enrolled にのみ変更して対応
                        factory.getEnrollmentAccess().setLastStatus(con, eventId, p, LastParticipationStatus.ENROLLED);
                        break;
                    case NOT_ENROLLED:
                        if (okMessageId == null) {
                            DirectMessage okEmbryo = new DirectMessage(event.getOwnerId(), enrollingMessage);
                            okMessageId = factory.getDirectMessageAccess().getFreshId(con);
                            factory.getDirectMessageAccess().addMessage(con, okMessageId, okEmbryo);
                        }

                        factory.getEnrollmentAccess().setLastStatus(con, eventId, p, LastParticipationStatus.ENROLLED);
                        factory.getDirectMessageAccess().sendEnvelope(con, okMessageId, p.getUserId(), p.getUserId(), event.getBeginDate(), DirectMessagePostingType.POSTING_TWITTER_DIRECT);
                        
                        break;
                    case ENROLLED:
                        break;
                    }
                }

                for (Participation p : list.getSpareParticipations()) {
                    LastParticipationStatus status = p.getLastStatus();
                    if (status == null) { continue; }

                    switch (status) {
                    case CHANGED: // 自分自身の力で変化させていた場合は status を not_enrolled にのみ変更して対応
                        factory.getEnrollmentAccess().setLastStatus(con, eventId, p, LastParticipationStatus.NOT_ENROLLED);
                        break;
                    case NOT_ENROLLED:
                        break;
                    case ENROLLED:
                        if (ngMessageId == null) {
                            DirectMessage ngEmbryo = new DirectMessage(event.getOwnerId(), cancellingMessage);
                            ngMessageId = factory.getDirectMessageAccess().getFreshId(con);
                            factory.getDirectMessageAccess().addMessage(con, ngMessageId, ngEmbryo); 
                        }

                        factory.getEnrollmentAccess().setLastStatus(con, eventId, p, LastParticipationStatus.NOT_ENROLLED);
                        factory.getDirectMessageAccess().sendEnvelope(con, ngMessageId, p.getUserId(), p.getUserId(), event.getBeginDate(), DirectMessagePostingType.POSTING_TWITTER_DIRECT);                    
                        break;
                    }
                }

                for (Participation p : list.getCancelledParticipations()) {
                    LastParticipationStatus status = p.getLastStatus();
                    if (status == null) { continue; }

                    switch (status) {
                    case CHANGED: // 自分自身の力で変化させていた場合は status を not_enrolled にのみ変更して対応
                        factory.getEnrollmentAccess().setLastStatus(con, eventId, p, LastParticipationStatus.NOT_ENROLLED);
                        break;
                    case NOT_ENROLLED:
                        break;
                    case ENROLLED:
                        if (ngMessageId == null) {
                            DirectMessage ngEmbryo = new DirectMessage(event.getOwnerId(), cancellingMessage);
                            ngMessageId = factory.getDirectMessageAccess().getFreshId(con);
                            factory.getDirectMessageAccess().addMessage(con, ngMessageId, ngEmbryo); 
                        }

                        factory.getEnrollmentAccess().setLastStatus(con, eventId, p, LastParticipationStatus.NOT_ENROLLED);
                        factory.getDirectMessageAccess().sendEnvelope(con, ngMessageId, p.getUserId(), p.getUserId(), event.getBeginDate(), DirectMessagePostingType.POSTING_TWITTER_DIRECT);                    
                        break;
                    }                   
                }
            }
        } finally {
            con.invalidate();
        }
    }
}
