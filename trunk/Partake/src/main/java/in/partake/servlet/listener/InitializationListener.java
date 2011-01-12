package in.partake.servlet.listener;

import in.partake.daemon.TwitterDaemon;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;


public class InitializationListener implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(InitializationListener.class);

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		TwitterDaemon.getInstance().schedule();
	};

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			TwitterDaemon.getInstance().cancel();
		} catch (Throwable ignore) {
			// catch and ignore for shutdown other daemons.
			logger.warn("TwitterMessageDaemon#cancel() threw a Throwable instance.", ignore);
		}
	}
}
