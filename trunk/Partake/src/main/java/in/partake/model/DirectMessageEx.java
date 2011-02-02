package in.partake.model;

import in.partake.model.dto.Message;

/**
 * direct message with related data.
 * @author shinyak
 *
 */
public class DirectMessageEx extends Message {
	private UserEx sender;
	
	public DirectMessageEx(Message message, UserEx sender) {
		super(message);
		this.sender = sender;
	}
	
	public UserEx getSender() {
		return sender;
	}
}
