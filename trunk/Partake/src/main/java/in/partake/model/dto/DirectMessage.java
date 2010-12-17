package in.partake.model.dto;

import java.util.Date;

public class DirectMessage extends PartakeModel<DirectMessage> {
    private String id;
    private String userId;		// senderId にすべきだな
    private String message;
    private String eventId;
    private Date   createdAt;
    
    public DirectMessage(String userId, String message) {
        this(userId, message, null);
    }

    public DirectMessage(String userId, String message, String eventId) {
        this(null, userId, message, eventId, null);
    }

    public DirectMessage(DirectMessage message) {
    	this(message.id, message.userId, message.message, message.eventId, message.createdAt);
    }
    
    public DirectMessage(String id, String userId, String message, String eventId, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.eventId = eventId;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        checkFrozen();
        this.message = message;
    }

    public String getEventId() {
        checkFrozen();
        return eventId;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public Date getCreatedAt() {
        checkFrozen();
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        checkFrozen();
        this.createdAt = createdAt;
    }
 

    
}

