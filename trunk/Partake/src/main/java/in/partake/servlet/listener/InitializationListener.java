package in.partake.servlet.listener;

import in.partake.daemon.TwitterMessageDaemon;
import in.partake.daemon.TwitterReminderDaemon;
import in.partake.model.dao.cassandra.CassandraDAOFactory;
import in.partake.service.PartakeService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class InitializationListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		TwitterMessageDaemon.getInstance().schedule();
		TwitterReminderDaemon.getInstance().schedule();
	};

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		TwitterMessageDaemon.getInstance().cancel();
		TwitterReminderDaemon.getInstance().cancel();
	}
}
