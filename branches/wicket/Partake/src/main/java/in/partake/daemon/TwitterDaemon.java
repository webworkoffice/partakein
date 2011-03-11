package in.partake.daemon;

import in.partake.model.dao.DAOException;
import in.partake.resource.I18n;
import in.partake.resource.PartakeProperties;
import in.partake.service.MessageService;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;


class TwitterDaemonTask extends TimerTask {
    private static final Logger logger = Logger.getLogger(TwitterDaemonTask.class);
    
    @Override
    public void run() {
        if (PartakeProperties.get().isEnabledTwitterDaemon()) {
            logger.info("TWITTER DAEMON TASK START.");
            runTwitterReminderTask();
            runStatusChangeTask();
            runTwitterMessageSendingTask();
            logger.info("TWITTER DAEMON TASK END.");
        } else {
            logger.debug("Twitter daemon task is disabled.");
        }
    }
    
    private void runTwitterReminderTask() {
        logger.info("TwitterReminderTask START");
        try {
            MessageService.get().sendReminders();
        } catch (DAOException e) {
            logger.warn(I18n.t(I18n.DATABASE_ERROR), e);
        }
        logger.info("TwitterReminderTask END");
    }
    
    private void runStatusChangeTask() {
        logger.info("ParticipationStatusChangeTask START.");

        try {
            MessageService.get().sendParticipationStatusChangeNotifications();
        } catch (DAOException e) {
            logger.warn(I18n.t(I18n.DATABASE_ERROR), e);
        }     
        logger.info("ParticipationStatusChangeTask END.");
    }
    
    
    private void runTwitterMessageSendingTask() {
        logger.info("DirectMessageSendingTask START");
        try {
            MessageService.get().sendEnvelopes();
        } catch (DAOException e) {
            logger.warn(I18n.t(I18n.DATABASE_ERROR), e);
        }        
        logger.info("DirectMessageSendingTask END");
    }
}

public class TwitterDaemon {
    private static final Logger logger = Logger.getLogger(TwitterDaemon.class);
    private static final int TIMER_INTERVAL_IN_MILLIS = 30000; // 30 secs. TODO: magic number!
    
    private static TwitterDaemon instance = new TwitterDaemon();
    private Timer timer;
        
    public static TwitterDaemon getInstance() {
        return instance;
    }
    
    private TwitterDaemon() {
        timer = new Timer();
    }
    
    public void schedule() {
        logger.info("Twitter daemons are scheduling...");
        // initial wait is required because application initialization may not be finished.
        timer.schedule(new TwitterDaemonTask(), TIMER_INTERVAL_IN_MILLIS, TIMER_INTERVAL_IN_MILLIS); 
    }
    
    public void cancel() {
        timer.cancel();
        logger.info("Scheduled twitter daemons have been cancelled.");
    }
}
