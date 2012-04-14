package in.partake.model;

import in.partake.model.dto.Message;
import in.partake.model.dto.UserReceivedMessage;

public class UserMessageEx extends UserReceivedMessage {
    private UserEx sender;
    private Message message;

    public UserMessageEx(UserReceivedMessage userMessage, UserEx sender, Message message) {
        super(userMessage);
        this.sender = sender;
        this.message = message;
    }

    public UserEx getSender() {
        return sender;
    }

    public Message getMessage() {
        return message;
    }
}
