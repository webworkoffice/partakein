package in.partake.heartbeat;

import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * <p>管理者に監視対象サイトがダウンしたことを通知するクラス。
 * 現在の実装ではTwitterのダイレクトメッセージを利用。</p>
 * @author skypencil(@eller86)
 */
class ReportSender {
	private final Logger logger = Logger.getLogger(getClass().getName());

	void report(Config config) {
		final Twitter twitter = new TwitterFactory().getInstance();
		final String message = String.format("%s トップページから %d秒以内 にレスポンスがありませんでした。", config.getUrl(), config.getTimeoutSec());
		for (String screenName : config.getScreenNames()) {
			try {
				twitter.sendDirectMessage(screenName, message);
			} catch (TwitterException ignore) {
				logger.log(Level.INFO, "管理者(" + screenName + ")にDM送信しようとして失敗しましたが無視します", ignore);
			}
		}
	}
}
