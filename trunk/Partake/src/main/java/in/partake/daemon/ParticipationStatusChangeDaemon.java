package in.partake.daemon;

import in.partake.model.dao.DAOException;
import in.partake.service.MessageService;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

class ParticipationStatusChangeTask extends TimerTask {
    private static final Logger logger = Logger.getLogger(ParticipationStatusChangeTask.class);
    
    @Override
    public void run() {
    	logger.info("ParticipationStatusChangeTask START.");

        try {
            MessageService.get().sendParticipationStatusChangeNotifications();
        } catch (DAOException e) {
            logger.warn("run() failed.", e);
        }     
        logger.info("ParticipationStatusChangeTask END.");
    }
    
}

public class ParticipationStatusChangeDaemon {
	private static final Logger logger = Logger.getLogger(ParticipationStatusChangeDaemon.class);
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
    	logger.info("scheduled.");
        timerForParticipationStatusMessages.schedule(new ParticipationStatusChangeTask(), 0, TIMER_INTERVAL_IN_MILLIS); 
    }
    
    public void cancel() {
    	logger.info("scheduled cancelled.");
        timerForParticipationStatusMessages.cancel();
    }
}
