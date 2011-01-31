package in.partake.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.apache.commons.lang.ObjectUtils;

// TODO: This class will be renamed as Message later.
@Entity
public class DirectMessage extends PartakeModel<DirectMessage> {
    @Id
    private String id;
    @Column
    private String userId;		// TODO: senderId にすべきだな
    @Column @Lob
    private String message;
    @Column
    private String eventId;
    @Column
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
    
    @Override
    public Object getPrimaryKey() {
        return id;
    }
    
    // ----------------------------------------------------------------------
    // equals method
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DirectMessage)) { return false; }
        
        DirectMessage lhs = this;
        DirectMessage rhs = (DirectMessage) obj;
        
        if (!(ObjectUtils.equals(lhs.id,        rhs.id)))        { return false; }
        if (!(ObjectUtils.equals(lhs.userId,    rhs.userId)))    { return false; }
        if (!(ObjectUtils.equals(lhs.message,   rhs.message)))   { return false; }
        if (!(ObjectUtils.equals(lhs.eventId,   rhs.eventId)))   { return false; }
        if (!(ObjectUtils.equals(lhs.createdAt, rhs.createdAt))) { return false; }
        return true;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        
        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(userId);
        code = code * 37 + ObjectUtils.hashCode(message);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(createdAt);
        
        return code;
    }
    
    // ----------------------------------------------------------------------
    // accessors

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

