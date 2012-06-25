package in.partake.servlet.listener;

import in.partake.app.PartakeApp;
import in.partake.daemon.PartakeDaemon;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;


public class InitializationListener implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(InitializationListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            PartakeApp.initialize();

            if (PartakeApp.getViewInitializer() != null) {
                String viewPath = event.getServletContext().getRealPath("css");
                PartakeApp.getViewInitializer().initialize(viewPath);
            }

            PartakeDaemon.getInstance().schedule();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        try {
            PartakeDaemon.getInstance().cancel();
        } catch (Throwable ignore) {
            // catch and ignore for shutdown other daemons.
            logger.warn("Unintentional exception is thrown.", ignore);
        }
    }
}
