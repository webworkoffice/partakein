package in.partake.heartbeat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class PartakeHeartbeatServlet extends HttpServlet {
	private static final long serialVersionUID = 3765387119544905943L;
	private final Logger logger = Logger.getLogger(getClass().toString());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		final PrintWriter writer = resp.getWriter();
		resp.setContentType("text/plain");

		try {
			final Config config = Config.Builder.build();
			checkSiteState(config);
			writer.println("success");
		} catch (Throwable unexpected) {
			// �\�����Ă��Ȃ���O
			logger.log(Level.SEVERE, "!! AN ERROR OCCURED !!", unexpected);
			writer.println("failed");
		}
	}

	private void checkSiteState(final Config config) {
		final SiteStateAccessor accessor = new SiteStateAccessor();
		final boolean siteWasAlive = accessor.loadPrevState();
		final boolean siteIsAlive = new SiteChecker().execute(config);

		if (!siteIsAlive && siteWasAlive) {
			// �_�E��
			new ReportSender().report(config);
		}
		accessor.storeSiteState(siteIsAlive);
	}
}
