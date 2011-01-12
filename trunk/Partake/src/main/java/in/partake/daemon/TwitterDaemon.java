package in.partake.daemon;

import in.partake.model.dao.DAOException;
import in.partake.service.DirectMessageService;
import in.partake.service.MessageService;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;


class TwitterDaemonTask extends TimerTask {
    private static final Logger logger = Logger.getLogger(TwitterDaemonTask.class);
    
    @Override
    public void run() {
        runTwitterReminderTask();
        runStatusChangeTask();
        runTwitterMessageSendingTask();
    }
    
    private void runTwitterReminderTask() {
        logger.info("TwitterReminderTask START");
        try {
            MessageService.get().sendReminders();
        } catch (DAOException e) {
            logger.warn("run() failed.", e);
        }
        logger.info("TwitterReminderTask END");
    }
    
    private void runStatusChangeTask() {
        logger.info("ParticipationStatusChangeTask START.");

        try {
            MessageService.get().sendParticipationStatusChangeNotifications();
        } catch (DAOException e) {
            logger.warn("run() failed.", e);
        }     
        logger.info("ParticipationStatusChangeTask END.");
    }
    
    private void runTwitterMessageSendingTask() {
        logger.info("DirectMessageSendingTask START");
        try {
            DirectMessageService.get().sendEnvelopes();
        } catch (DAOException e) {
            logger.warn("run() failed.", e);
        }        
        logger.info("DirectMessageSendingTask END");
    }
}

public class TwitterDaemon {
    private static final Logger logger = Logger.getLogger(TwitterDaemon.class);
    private static final int TIMER_INTERVAL_IN_MILLIS = 30000; // 30 secs.
    
    private static TwitterDaemon instance = new TwitterDaemon();
    private Timer timer;
        
    public static TwitterDaemon getInstance() {
        return instance;
    }
    
    private TwitterDaemon() {
        timer = new Timer();
    }
    
    public void schedule() {
        logger.info("scheduled.");
        timer.schedule(new TwitterDaemonTask(), 0, TIMER_INTERVAL_IN_MILLIS); 
    }
    
    public void cancel() {
        logger.info("scheduled cancelled.");
        timer.cancel();
    }
}
