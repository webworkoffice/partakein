package in.partake.heartbeat;

import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * <p>�Ǘ��҂ɊĎ��ΏۃT�C�g���_�E���������Ƃ�ʒm����N���X�B
 * ���݂̎����ł�Twitter�̃_�C���N�g���b�Z�[�W�𗘗p�B</p>
 * @author skypencil(@eller86)
 */
class ReportSender {
	private final Logger logger = Logger.getLogger(getClass().toString());

	void report(Config config) {
		final Twitter twitter = new TwitterFactory().getInstance();
		final String message = String.format("%s �g�b�v�y�[�W���� %d�b�ȓ� �Ƀ��X�|���X������܂���ł����B", config.getUrl(), config.getTimeoutSec());
		for (String screenName : config.getScreenNames()) {
			try {
				twitter.sendDirectMessage(screenName, message);
			} catch (TwitterException ignore) {
				logger.log(Level.INFO, "�Ǘ���(" + screenName + ")��DM���M���悤�Ƃ��Ď��s���܂������������܂�", ignore);
			}
		}
	}
}
