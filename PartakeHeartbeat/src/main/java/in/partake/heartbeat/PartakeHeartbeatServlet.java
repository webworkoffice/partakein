package in.partake.heartbeat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>サービスの中心となるクラス。
 * 複数のクラスを組み合わせ、監視対象サイトがダウンした際にレポートを送信する。</p>
 * @author skypencil(@eller86)
 */
public class PartakeHeartbeatServlet extends HttpServlet {
	private static final long serialVersionUID = 3765387119544905943L;
	private final Logger logger = Logger.getLogger(getClass().getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		final PrintWriter writer = resp.getWriter();
		resp.setContentType("text/plain");

		try {
			final Config config = new Config.Builder().build();
			checkSiteState(config);
			writer.println("success");
		} catch (Throwable unexpected) {
			// 予期していない例外
			logger.log(Level.SEVERE, "!! AN ERROR OCCURED !!", unexpected);
			writer.println("failed");
		}
	}

	private void checkSiteState(Config config) {
		final SiteStateAccessor accessor = new SiteStateAccessor();
		final boolean siteWasAlive = accessor.loadPreviousState();
		final boolean siteIsAlive = new SiteChecker().execute(config);

		if (!siteIsAlive && siteWasAlive) {
			// 監視対象サイトがダウン
			new ReportSender().report(config);
		}
		accessor.storeSiteState(siteIsAlive);
	}
}
