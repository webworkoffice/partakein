package in.partake.heartbeat;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

final class SiteChecker {
	private final Logger logger = Logger.getLogger(getClass().toString());

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
				throw new IllegalResponseException(statusCode);
			}

			while (stream.read() != -1) {
				// fetch all request
			}
			success = true;
			logger.log(Level.FINE, "complete.");
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
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
			throws MalformedURLException, IOException {
		final URL url = new URL(config.getUrl());
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(config.getTimeoutSec() * 1000);
		conn.setReadTimeout(config.getTimeoutSec() * 1000);
		conn.setDoOutput(false);
		conn.setUseCaches(false);
		return conn;
	}

}