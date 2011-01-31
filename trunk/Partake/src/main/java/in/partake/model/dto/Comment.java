package in.partake.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.apache.commons.lang.ObjectUtils;

@Entity
public class Comment extends PartakeModel<Comment> {
    @Id 
    private String id;          // comment id
    @Column
    private String eventId;     // the event id this comment is associated to
    @Column
    private String userId;      // who wrote this comment
    @Column @Lob
    private String comment;     // content
    @Column
    private Date   createdAt;   // when
    
    public Comment() {
        this(null, null, null, null, null);
    }
    
    public Comment(String eventId, String userId, String comment, Date createdAt) {
        this(null, eventId, userId, comment, createdAt);
    }

    public Comment(String id, String eventId, String userId, String comment, Date createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.comment = comment;
        this.createdAt = createdAt == null ? null : (Date) createdAt.clone();
    }
    
    public Comment(Comment comment) {
    	this.id = comment.id;
    	this.eventId = comment.eventId;
    	this.userId = comment.userId;
    	this.comment = comment.comment;
    	this.createdAt = comment.createdAt == null ? null : (Date) comment.createdAt.clone();
    }
    
    @Override
    public Object getPrimaryKey() {
        return id;
    }
    
    // ----------------------------------------------------------------------
    // equals method

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Comment)) { return false; }
        
        Comment lhs = this;
        Comment rhs = (Comment) obj;
        
        if (!ObjectUtils.equals(lhs.id, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.eventId, rhs.eventId)) { return false; }
        if (!ObjectUtils.equals(lhs.userId, rhs.userId)) { return false; }
        if (!ObjectUtils.equals(lhs.comment, rhs.comment)) { return false; }
        if (!ObjectUtils.equals(lhs.createdAt, rhs.createdAt)) { return false; }

        return true;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        
        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(userId);
        code = code * 37 + ObjectUtils.hashCode(comment);
        code = code * 37 + ObjectUtils.hashCode(createdAt);
        
        return code;
    }
    
    
    // ----------------------------------------------------------------------
    // accessors

	public String getId() {
	    return id;
	}
	
	public String getEventId() {
	    return eventId;
	}

	public String getUserId() {
	    return userId;
	}
	
	public String getComment() {
	    return comment;
	}
	
	public Date getCreatedAt() {
	    return createdAt;
	}
	
	public void setId(String id) {
	    checkFrozen();
	    this.id = id;
	}
	
	public void setEventId(String eventId) {
	    checkFrozen();
	    this.eventId = eventId;
	}

    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }

    public void setComment(String comment) {
        checkFrozen();
        this.comment = comment;
    }

    public void setCreatedAt(Date createdAt) {
        checkFrozen();
        this.createdAt = createdAt;
    }
}
