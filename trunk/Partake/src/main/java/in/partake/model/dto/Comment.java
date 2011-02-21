package in.partake.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.apache.commons.lang.ObjectUtils;

@Entity(name = "Comments")
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
    private boolean isHTML;     // true if HTML.
    @Column
    private Date   createdAt;   // when
    
    public Comment() {
        this(null, null, null, null, false, null);
    }
    
    public Comment(String eventId, String userId, String comment, boolean isHTML, Date createdAt) {
        this(null, eventId, userId, comment, isHTML, createdAt);
    }

    public Comment(String id, String eventId, String userId, String comment, boolean isHTML, Date createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.comment = comment;
        this.isHTML = isHTML;
        this.createdAt = createdAt == null ? null : (Date) createdAt.clone();
    }
    
    public Comment(Comment comment) {
    	this.id = comment.id;
    	this.eventId = comment.eventId;
    	this.userId = comment.userId;
    	this.comment = comment.comment;
    	this.isHTML = comment.isHTML;
    	this.createdAt = comment.createdAt == null ? null : (Date) comment.createdAt.clone();
    }
    
    @Override
    public Object getPrimaryKey() {
        return id;
    }
    
    @Override
    public Comment copy() {
        return new Comment(this);
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
        if (!ObjectUtils.equals(lhs.isHTML, rhs.isHTML)) { return false; }
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
        code = code * 37 + ObjectUtils.hashCode(isHTML);
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
	
	public boolean isHTML() {
	    return isHTML;
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

    public void setHTML(boolean isHTML) {
        checkFrozen();
        this.isHTML = isHTML;
    }
    
    public void setCreatedAt(Date createdAt) {
        checkFrozen();
        this.createdAt = createdAt;
    }
}
