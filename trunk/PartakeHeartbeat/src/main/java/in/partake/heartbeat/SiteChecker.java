package in.partake.heartbeat;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>監視対象サイトの生死を確認するクラス。
 * HTTPリクエストを送り、以下の場合にサイトがダウンしたと判断する。</p>
 * <ul>
 * <li>一定時間内にレスポンスが帰ってこなかった場合</li>
 * <li>200以外のステータスコードが帰ってきた場合</li>
 * <li>サーバとの接続でエラーが発生した場合（HttpURLConnectionなどがIOExceptionを投げた場合）</li>
 * </ul>
 * @author skypencil(@eller86)
 */
final class SiteChecker {
	private final Logger logger = Logger.getLogger(getClass().getName());

	boolean execute(Config config) throws IllegalArgumentException {
		logger.log(Level.FINE, "start to check " + config.getUrl());

		HttpURLConnection conn = null;
		InputStream stream = null;
		boolean success = false;
		try {
			conn = createConnection(config);
			stream = new BufferedInputStream(conn.getInputStream());

			final int statusCode = conn.getResponseCode();
			if (statusCode != HttpURLConnection.HTTP_OK) {
				throw new IllegalResponseException(statusCode, conn.getResponseMessage());
			}

			while (stream.read() != -1) {
				// fetch all response
			}
			success = true;
			logger.log(Level.FINE, "complete.");
		} catch (IOException e) {
			// TODO GAE側が頻繁にエラーを吐くなど、このケースにダウンと判断することが望ましくない場合は変更を検討する
			logger.log(Level.INFO, "IOException occures.", e);
		} catch (IllegalResponseException e) {
			logger.log(Level.INFO, "SiteChecker receives the illegal response.", e);
		} finally {
			cleanUp(conn, stream);
		}

		return success;
	}

	private void cleanUp(HttpURLConnection conn, InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException ignore) {
				logger.log(Level.INFO, "InputStream#close throws the exception.", ignore);
			}
		}
		if (conn != null) {
			conn.disconnect();
		}
	}

	private HttpURLConnection createConnection(Config config)
			throws IOException {
		final HttpURLConnection conn = (HttpURLConnection) config.getUrl().openConnection();
		conn.setConnectTimeout(config.getTimeoutSec() * 1000);
		conn.setReadTimeout(config.getTimeoutSec() * 1000);
		conn.setDoOutput(false);
		conn.setUseCaches(false);
		return conn;
	}

}