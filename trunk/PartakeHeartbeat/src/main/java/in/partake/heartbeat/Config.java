package in.partake.heartbeat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>サービスの設定項目を保持するクラス。インスタンスの生成には{@link Config.Builder}クラスを利用すること。
 * 現時点では以下の項目を保持する。</p>
 * <ul>
 * <li>応答を確認するURL</li>
 * <li>監視対象サイトから応答がなかった時にDMを送る相手のscreen name</li>
 * <li>応答なしと判断する基準（レスポンスを受け取るまでの秒数）</li>
 * </ul>
 * @author skypencil(@eller86)
 * @see Config.Builder
 */
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

	/**
	 * <p>外部ファイルから設定を読み込んで{@link Config}クラスのインスタンスを生成するクラス。</p>
	 * @author skypencil(@eller86)
	 * @see Config
	 */
	static class Builder {
		private static final String KEY_OF_URL = "in.partake.heartbeat.url";
		private static final String KEY_OF_SCREEN_NAMES = "in.partake.heartbeat.screen_names";

		Config build() {
			// appengine-web.xmlに記載したシステムプロパティから設定を読み込む
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
