package in.partake.model;

import in.partake.model.dto.DirectMessage;

public class DirectMessageEx extends DirectMessage {
	private UserEx sender;
	
	public DirectMessageEx(DirectMessage message, UserEx sender) {
		super(message);
		this.sender = sender;
	}
	
	public UserEx getSender() {
		return sender;
	}
}
