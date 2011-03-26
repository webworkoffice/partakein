package in.partake.heartbeat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class Config {
	private static final int TIMEOUT_SEC = 5;
	private final String targetUrl;
	private final List<String> targetScreenNames;

	private Config(String url, List<String> screenNames) {
		this.targetUrl = url;
		this.targetScreenNames = Collections.unmodifiableList(screenNames);
	}

	String getUrl() {
		return this.targetUrl;
	}

	List<String> getScreenNames() {
		return this.targetScreenNames;
	}

	int getTimeoutSec() {
		return TIMEOUT_SEC;
	}

	static class Builder {
		private static final String KEY_OF_URL = "in.partake.heartbeat.url";
		private static final String KEY_OF_SCREEN_NAMES = "in.partake.heartbeat.screen_names";

		static Config build() {
			final String url = System.getProperty(KEY_OF_URL);
			final String screenNames = System.getProperty(KEY_OF_SCREEN_NAMES);

			if (url == null || url.isEmpty()) {
				throw new IllegalStateException(KEY_OF_URL + " is null or empty.");
			}
			if (screenNames == null || screenNames.isEmpty()) {
				throw new IllegalStateException(KEY_OF_SCREEN_NAMES + " is null or empty.");
			}

			return new Config(url, Arrays.asList(screenNames.split(",")));
		}
	}

}
