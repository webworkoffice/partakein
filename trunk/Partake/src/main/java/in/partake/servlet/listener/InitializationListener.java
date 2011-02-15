package in.partake.servlet.listener;

import java.io.File;
import java.io.FilenameFilter;

import in.partake.daemon.TwitterDaemon;
import in.partake.view.Helper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;


public class InitializationListener implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(InitializationListener.class);

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		TwitterDaemon.getInstance().schedule();
		initializeCssVersion(arg0);
	}

	private void initializeCssVersion(ServletContextEvent event) {
		String cssPath = event.getServletContext().getRealPath("css");
		File cssDir = new File(cssPath);
		long version = 0L;
		if (cssDir.exists()) {
			for (File cssFile : cssDir.listFiles(new FilenameFilter(){
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".css");
				}
			})) {
				version ^= cssFile.lastModified();
			}
		} else {
			logger.warn("cannot find the css/ directory.");
		}
		Helper.setCssVersion(Long.toString(version));
	};

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			TwitterDaemon.getInstance().cancel();
		} catch (Throwable ignore) {
			// catch and ignore for shutdown other daemons.
			logger.warn("Unintentional exception is thrown.", ignore);
		}
	}
}
