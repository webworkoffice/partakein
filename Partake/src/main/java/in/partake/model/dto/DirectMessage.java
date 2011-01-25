package in.partake.model.dto;

import java.util.Date;

// TODO: This class will be renamed as Message later.
public class DirectMessage extends PartakeModel<DirectMessage> {
    private String id;
    private String userId;		// TODO: senderId にすべきだな
    private String message;
    private String eventId;
    private Date   createdAt;
    
    public DirectMessage() {
        // do nothing
    }
    
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

    public String getUserId() {
        return userId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }

    public void setMessage(String message) {
        checkFrozen();
        this.message = message;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }



    public void setCreatedAt(Date createdAt) {
        checkFrozen();
        this.createdAt = createdAt;
    }
 

    
}

