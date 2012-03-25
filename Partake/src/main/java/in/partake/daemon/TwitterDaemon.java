package in.partake.daemon;

import in.partake.base.PartakeException;
import in.partake.model.dao.DAOException;
import in.partake.resource.PartakeProperties;
import in.partake.resource.ServerErrorCode;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;


class TwitterDaemonTask extends TimerTask {
    private static final Logger logger = Logger.getLogger(TwitterDaemonTask.class);
    
    @Override
    public void run() {
        if (PartakeProperties.get().isEnabledTwitterDaemon()) {
            logger.info("TWITTER DAEMON TASK START.");
            try {
                runTwitterReminderTask();
                runStatusChangeTask();
                runTwitterMessageSendingTask();
            } catch (PartakeException e) {
                String reasonString = 
                        e.isServerError() ? e.getServerErrorCode().getReasonString() : e.getUserErrorCode().getReasonString();
                logger.warn(reasonString, e);
            } catch (DAOException e) {
                // run() には DAOException を出させたくない
                logger.warn(ServerErrorCode.DB_ERROR, e);
            }
            logger.info("TWITTER DAEMON TASK END.");
        } else {
            logger.debug("Twitter daemon task is disabled.");
        }
    }
    
    private void runTwitterReminderTask() throws DAOException, PartakeException {
        logger.info("TwitterReminderTask START");
        new TwitterReminderTask().execute();
        logger.info("TwitterReminderTask END");
    }
    
    private void runStatusChangeTask() throws DAOException, PartakeException {
        logger.info("ParticipationStatusChangeTask START.");
        new SendParticipationStatusChangeNotificationsTask().execute();
        logger.info("ParticipationStatusChangeTask END.");
    }
    
    
    private void runTwitterMessageSendingTask() throws DAOException, PartakeException {
        logger.info("DirectMessageSendingTask START");
        new SendEnvelopeTask().execute();
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
