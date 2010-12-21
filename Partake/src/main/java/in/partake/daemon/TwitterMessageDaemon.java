package in.partake.daemon;


import in.partake.model.dao.DAOException;
import in.partake.service.DirectMessageService;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;


class DirectMessageSendingTask extends TimerTask {
    private static final Logger logger = Logger.getLogger(DirectMessageSendingTask.class);
    
    @Override
    public void run() {
    	logger.info("DirectMessageSendingTask START");
        try {
            DirectMessageService.get().sendEnvelopes();
        } catch (DAOException e) {
            logger.warn("run() failed.", e);
        }        
        logger.info("DirectMessageSendingTask END");
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
