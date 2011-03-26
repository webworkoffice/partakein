package in.partake.heartbeat;

final class IllegalResponseException extends Exception {

	private static final long serialVersionUID = 5579117979745065153L;
	private final int statusCode;

	IllegalResponseException(int statusCode) {
		super("Illegal status code:" + statusCode);
		this.statusCode = statusCode;
	}

	int getStatusCode() {
		return this.statusCode;
	}
}
