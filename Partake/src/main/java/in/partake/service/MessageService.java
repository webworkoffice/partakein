package in.partake.service;

import java.util.Date;
import java.util.List;

import in.partake.model.EventEx;
import in.partake.model.ParticipationEx;
import in.partake.model.ParticipationList;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.KeyIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeModelFactory;
import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.DirectMessagePostingType;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventNotificationStatus;
import in.partake.model.dto.LastParticipationStatus;
import in.partake.model.dto.Participation;
import in.partake.model.dto.ParticipationStatus;
import in.partake.resource.PartakeProperties;
import in.partake.util.Util;

public final class MessageService extends PartakeService {
    private static MessageService instance = new MessageService();
    
    private MessageService() {
        // do nothing for now. 
    }
    
    public static MessageService get() {
        return instance;
    }
    
    /**
     * send a reminder for necessary events.
     * @throws DAOException
     */
    public void sendReminders() throws DAOException {
        String topPath = PartakeProperties.get().getTopPath();      
        Date now = new Date();

        DataIterator<EventNotificationStatus> iterator = getFactory().getMessageAccess().getNotificationStatuses(getFactory()); 
        while (iterator.hasNext()) {                
            EventNotificationStatus status = iterator.next();
            
            boolean changed = sendEventNotification(status, topPath, now);
            
            // remove で落ちると無限に message が送られるので、update() を別にしておく
            if (changed) {
                iterator.update(status);
            }
            
            if (status.isBeforeDeadlineOneday() && status.isBeforeDeadlineHalfday() && status.isBeforeTheDay()) {
                iterator.remove();
            }
        }
    }
    
    private boolean sendEventNotification(EventNotificationStatus status, String topPath, Date now) throws DAOException {
        Event event = EventService.get().getEventById(status.getEventId());
        if (event == null) { return false; }

        Date beginDate = event.getBeginDate();
        Date deadline = event.getDeadline();
        if (deadline == null) { deadline = beginDate; }
        
        boolean changed = false;
        
        String shortenedURL = Util.shortenURL(event.getEventURL());
        
        // TODO: isBeforeDeadline() とかわかりにくいな。 
        // 締め切り１日前になっても RESERVED ステータスの人がいればメッセージを送付する。
        if (!status.isBeforeDeadlineOneday() && Util.oneDayBefore(deadline).before(now)) {
            String message = "[PARTAKE] 締め切り１日前です。参加・不参加を確定してください。 " + shortenedURL + " " + event.getTitle();
            message = Util.shorten(message, 140);
            sendNotificationOnlyForReservedParticipants(event, message);
                                
            status.setBeforeDeadlineOneday(true);
            changed = true;
        }
        
        // 締め切り１２時間前になっても RESERVED な人がいればメッセージを送付する。
        if (!status.isBeforeDeadlineHalfday() && Util.halfDayBefore(deadline).before(now)) {
            String message = "[PARTAKE] 締め切り１２時間前です。参加・不参加を確定してください。 ３時間前までに確定されない場合、キャンセル扱いとなります。" + shortenedURL + " " + event.getTitle();
            message = Util.shorten(message, 140);
            sendNotificationOnlyForReservedParticipants(event, message);
                                
            status.setBeforeDeadlineHalfday(true);
            changed = true;            
        }
        
        // イベント１日前で、参加が確定している人にはメッセージを送付する。
        // 参加が確定していない人には、RESERVED なメッセージが送られている。
        if (!status.isBeforeTheDay() && Util.oneDayBefore(beginDate).before(now)) {
            String message = "[PARTAKE] イベントの１日前です。あなたの参加は確定しています。 " + shortenedURL + " " + event.getTitle();
            message = Util.shorten(message, 140);
            sendNotificationOnlyForParticipants(event, message);
            
            status.setBeforeTheDay(true);
            changed = true;
        }
        
        return changed;
    }

    /**
     * message の内容で、仮参加者にのみメッセージを送る。
     * @param event
     * @param message
     * @throws DAOException
     */
    private void sendNotificationOnlyForReservedParticipants(Event event, String message) throws DAOException {
        DirectMessage embryo = new DirectMessage(event.getOwnerId(), message);
        String messageId = DirectMessageService.get().addMessage(embryo, false);
        
        List<Participation> participations = EventService.get().getParticipation(event.getId());
        Date deadline = event.getDeadline();
        if (deadline == null) { deadline = event.getBeginDate(); }
        for (Participation participation : participations) {
            if (ParticipationStatus.RESERVED.equals(participation.getStatus())){
                DirectMessageService.get().sendEnvelope(
                                messageId, participation.getUserId(), participation.getUserId(), deadline,                                
                                DirectMessagePostingType.POSTING_TWITTER_DIRECT);
            }
        }
    }
    
    /**
     * message の内容で、参加確定者にのみメッセージを送る
     * @param event
     * @param message
     * @throws DAOException
     */
    private void sendNotificationOnlyForParticipants(Event event, String message) throws DAOException {
        DirectMessage embryo = new DirectMessage(event.getOwnerId(), message);
        String messageId = DirectMessageService.get().addMessage(embryo, false);
        
        int capacity = event.getCapacity();
        int num = 0;
        
        Date deadline = event.getDeadline();
        if (deadline == null) { deadline = event.getBeginDate(); }
        
        // TODO: 判定には participationlist を使うべき。
        // TODO: あと、補欠者にも送った方がいいんじゃなイカ？
        List<Participation> participations = EventService.get().getParticipation(event.getId());
        for (Participation participation : participations) {
            boolean sendsMessage = false;
            switch (participation.getStatus()) {
            case ENROLLED:
                if (capacity == 0 || num < capacity) { sendsMessage = true; }
                ++num;
                break;
            case RESERVED:
                ++num;
                break;
            default:
                break;
            }
            
            if (sendsMessage) {
                DirectMessageService.get().sendEnvelope(
                            messageId, participation.getUserId(), participation.getUserId(), deadline,
                            DirectMessagePostingType.POSTING_TWITTER_DIRECT);
            }
        }
    }
    
    
    /**
     * イベントIDから通知（リマインダ）の送信状況を取得する。
     * @param eventId イベントのID
     * @return 指定したイベントに関する通知の状況
     * @throws DAOException
     */
    // TODO: should this be here? EventService?
    public EventNotificationStatus getNotificationStatus(String eventId) throws DAOException {
        PartakeModelFactory factory = getFactory();
        PartakeConnection con = factory.getConnection();
        try {
            return factory.getMessageAccess().getNotificationStatus(con, eventId);
        } finally {
            con.invalidate();
        }
    }
    
    // TODO: 開催前のイベントだけiterateすれば充分かも
    public void sendParticipationStatusChangeNotifications() throws DAOException {
        Date now = new Date();
        
        PartakeModelFactory factory = getFactory();
        PartakeConnection con = factory.getConnection();
        try {
            KeyIterator it = factory.getEventAccess().getAllEventKeys(factory);
            while (it.hasNext()) {
                String eventId = it.next();
                if (eventId == null) { continue; }
                EventEx event = getEventEx(con, eventId); 
                if (event == null) { continue; }
                
                if (!now.before(event.getBeginDate())) { continue; }

                List<ParticipationEx> participations = EventService.get().getParticipationEx(event.getId());
                ParticipationList list = event.calculateParticipationList(participations);

                String enrollingMessage = "[PARTAKE] 補欠から参加者へ繰り上がりました。 " + Util.shortenURL(event.getEventURL()) + " " + event.getTitle(); 
                String cancellingMessage = "[PARTAKE] 参加者から補欠扱い(あるいはキャンセル扱い)に変更になりました。 " + Util.shortenURL(event.getEventURL()) + " " + event.getTitle(); 
                enrollingMessage = Util.shorten(enrollingMessage, 140);
                cancellingMessage = Util.shorten(cancellingMessage, 140);

                String okMessageId = null;
                String ngMessageId = null;

                // TODO: ここのソース汚い。同一化できる。とくに、あとの２つは一緒。
                for (Participation p : list.getEnrolledParticipations()) {
                    LastParticipationStatus status = p.getLastStatus();
                    if (status == null) { continue; }

                    switch (status) {
                    case CHANGED: // 自分自身の力で変化させていた場合は status を enrolled にのみ変更して対応
                        EventService.get().setLastStatus(event.getId(), p, LastParticipationStatus.ENROLLED);
                        break;
                    case NOT_ENROLLED:
                        if (okMessageId == null) {
                            DirectMessage okEmbryo = new DirectMessage(event.getOwnerId(), enrollingMessage);
                            okMessageId = DirectMessageService.get().addMessage(okEmbryo, false);                        
                        }

                        EventService.get().setLastStatus(event.getId(), p, LastParticipationStatus.ENROLLED);                    
                        DirectMessageService.get().sendEnvelope(okMessageId, p.getUserId(), p.getUserId(), event.getBeginDate(), DirectMessagePostingType.POSTING_TWITTER_DIRECT);
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
                        EventService.get().setLastStatus(event.getId(), p, LastParticipationStatus.NOT_ENROLLED);
                        break;
                    case NOT_ENROLLED:
                        break;
                    case ENROLLED:
                        if (ngMessageId == null) {
                            DirectMessage ngEmbryo = new DirectMessage(event.getOwnerId(), cancellingMessage);
                            ngMessageId = DirectMessageService.get().addMessage(ngEmbryo, false);
                        }

                        EventService.get().setLastStatus(event.getId(), p, LastParticipationStatus.NOT_ENROLLED);                    
                        DirectMessageService.get().sendEnvelope(ngMessageId, p.getUserId(), p.getUserId(), event.getBeginDate(), DirectMessagePostingType.POSTING_TWITTER_DIRECT);                    
                        break;
                    }
                }

                for (Participation p : list.getCancelledParticipations()) {
                    LastParticipationStatus status = p.getLastStatus();
                    if (status == null) { continue; }

                    switch (status) {
                    case CHANGED: // 自分自身の力で変化させていた場合は status を not_enrolled にのみ変更して対応
                        EventService.get().setLastStatus(event.getId(), p, LastParticipationStatus.NOT_ENROLLED);
                        break;
                    case NOT_ENROLLED:
                        break;
                    case ENROLLED:
                        if (ngMessageId == null) {
                            DirectMessage ngEmbryo = new DirectMessage(event.getOwnerId(), cancellingMessage);
                            ngMessageId = DirectMessageService.get().addMessage(ngEmbryo, false);
                        }

                        EventService.get().setLastStatus(event.getId(), p, LastParticipationStatus.NOT_ENROLLED);                    
                        DirectMessageService.get().sendEnvelope(ngMessageId, p.getUserId(), p.getUserId(), event.getBeginDate(), DirectMessagePostingType.POSTING_TWITTER_DIRECT);                    
                        break;
                    }                   
                }
            }
        } finally {
            con.invalidate();
        }
    }
}
