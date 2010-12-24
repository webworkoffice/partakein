package in.partake.daemon;

import in.partake.model.dao.DAOException;
import in.partake.service.MessageService;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;



/**
 * 締め切りがすぎた後、および当日に reminder を流します。
 * 
 * @author shinyak
 *
 */
class TwitterReminderTask extends TimerTask {
    private static final Logger logger = Logger.getLogger(TwitterReminderTask.class);
    
	@Override
	public void run() {
	    logger.info("TwitterReminderTask START");
		try {
		    MessageService.get().sendReminders();
		} catch (DAOException e) {
			logger.warn("run() failed.", e);
		}
		logger.info("TwitterReminderTask END");
	}
	

}

public class TwitterReminderDaemon {
	private static final Logger logger = Logger.getLogger(TwitterReminderDaemon.class);
	
	private static final int TIMER_INTERVAL_IN_MILLIS = 30000; // ３０秒
	//private static final int TIMER_INTERVAL_IN_MILLIS = 60000; // ６０秒
    
	private static TwitterReminderDaemon instance;
	private Timer timerForReminderDaemon;
		
	static {
		instance = new TwitterReminderDaemon();
	}
	
	public static TwitterReminderDaemon getInstance() {
		return instance;
	}
	
	private TwitterReminderDaemon() {
		timerForReminderDaemon = new Timer();
	}
	
	public void schedule() {
	    logger.info("scheduled.");
		timerForReminderDaemon.schedule(new TwitterReminderTask(), 0, TIMER_INTERVAL_IN_MILLIS);
	}
	
	public void cancel() {
	    logger.info("scheduled cancelled.");
		timerForReminderDaemon.cancel();
	}
}
