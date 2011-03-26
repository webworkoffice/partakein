package in.partake.heartbeat;

/**
 * <p>�Ď��ΏۃT�C�g�����҂ƈقȂ�X�e�[�^�X�R�[�h��Ԃ������Ƃ�\����O�B</p>
 * @author skypencil(@eller86)
 */
final class IllegalResponseException extends Exception {

	private static final long serialVersionUID = 5579117979745065153L;
	private final int statusCode;

	IllegalResponseException(int statusCode, String message) {
		super(String.format("Illegal status code:%d, response message:%s", statusCode, message));
		this.statusCode = statusCode;
	}

	int getStatusCode() {
		return this.statusCode;
	}
}
