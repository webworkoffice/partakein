package in.partake.daemon;

import in.partake.model.EventEx;
import in.partake.model.ParticipationEx;
import in.partake.model.ParticipationList;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.KeyIterator;
import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.DirectMessagePostingType;
import in.partake.model.dto.LastParticipationStatus;
import in.partake.model.dto.Participation;
import in.partake.service.DirectMessageService;
import in.partake.service.EventService;
import in.partake.util.Util;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

class ParticipationStatusChangeTask extends TimerTask {
    private static final Logger logger = Logger.getLogger(ParticipationStatusChangeTask.class);
    
    @Override
    public void run() {
    	logger.info("ParticipationStatusChangeTask START.");
        Date now = new Date();
        try {
        	// TODO: 開催前のイベントだけiterateすれば充分かも
            KeyIterator it = EventService.get().getAllEventKeysIterator();
            while (it.hasNext()) {
                EventEx event = EventService.get().getEventExById(it.next());
                if (!now.before(event.getBeginDate())) { continue; }
                
                List<ParticipationEx> participations = EventService.get().getParticipationEx(event.getId());
                ParticipationList list = event.calculateParticipationList(participations);
                
                String enrollingMessage = "[PARTAKE] 補欠から参加者へ繰り上がりました。 " + Util.bitlyShortURL(event.getEventURL()) + " " + event.getTitle(); 
                String cancellingMessage = "[PARTAKE] 参加者から補欠扱い(あるいはキャンセル扱い)に変更になりました。 " + Util.bitlyShortURL(event.getEventURL()) + " " + event.getTitle(); 
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
            
        } catch (DAOException e) {
            logger.warn("run() failed.", e);
        }     
        logger.info("ParticipationStatusChangeTask END.");
    }
    
}

public class ParticipationStatusChangeDaemon {
    private static final int TIMER_INTERVAL_IN_MILLIS = 120000; // ２分
    
    private static ParticipationStatusChangeDaemon instance = new ParticipationStatusChangeDaemon();
    private Timer timerForParticipationStatusMessages;
        
    public static ParticipationStatusChangeDaemon getInstance() {
        return instance;
    }
    
    private ParticipationStatusChangeDaemon() {
        timerForParticipationStatusMessages = new Timer();
    }
    
    public void schedule() {
        timerForParticipationStatusMessages.schedule(new ParticipationStatusChangeTask(), 0, TIMER_INTERVAL_IN_MILLIS); 
    }
    
    public void cancel() {
        timerForParticipationStatusMessages.cancel();
    }
}
