package in.partake.servlet.listener;

import in.partake.daemon.TwitterMessageDaemon;
import in.partake.daemon.TwitterReminderDaemon;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;


public class InitializationListener implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(InitializationListener.class);

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		TwitterMessageDaemon.getInstance().schedule();
		TwitterReminderDaemon.getInstance().schedule();
	};

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			TwitterMessageDaemon.getInstance().cancel();
		} catch (Throwable ignore) {
			// catch and ignore for shutdown other daemons.
			logger.warn("TwitterMessageDaemon#cancel() throws a Throwable instance.", ignore);
		}

		try {
			TwitterReminderDaemon.getInstance().cancel();
		} catch (Throwable ignore) {
			// catch and ignore for shutdown other daemons.
			logger.warn("TwitterReminderDaemon#cancel() throws a Throwable instance.", ignore);
		}
	}
}
