package in.partake.heartbeat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>�T�[�r�X�̐ݒ荀�ڂ�ێ�����N���X�B�C���X�^���X�̐����ɂ�{@link Config.Builder}�N���X�𗘗p���邱�ƁB
 * �����_�ł͈ȉ��̍��ڂ�ێ�����B</p>
 * <ul>
 * <li>�������m�F����URL</li>
 * <li>�Ď��ΏۃT�C�g���牞�����Ȃ���������DM�𑗂鑊���screen name</li>
 * <li>�����Ȃ��Ɣ��f�����i���X�|���X���󂯎��܂ł̕b���j</li>
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
	 * <p>�O���t�@�C������ݒ��ǂݍ����{@link Config}�N���X�̃C���X�^���X�𐶐�����N���X�B</p>
	 * @author skypencil(@eller86)
	 * @see Config
	 */
	static class Builder {
		private static final String KEY_OF_URL = "in.partake.heartbeat.url";
		private static final String KEY_OF_SCREEN_NAMES = "in.partake.heartbeat.screen_names";

		Config build() {
			// appengine-web.xml�ɋL�ڂ����V�X�e���v���p�e�B����ݒ��ǂݍ���
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
