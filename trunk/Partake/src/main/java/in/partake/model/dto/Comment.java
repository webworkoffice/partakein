package in.partake.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Comment extends PartakeModel<Comment> {
    @Id     private String id;          // comment id
    @Column private String eventId;     // どの event へのコメントか
    @Column private String userId;      // だれが書いたコメントか
    @Column private String comment;     // コメントの内容    
    @Column private Date   createdAt;   // コメント日時
    
    public Comment() {
        this(null, null, null, null, null);
    }
    
    public Comment(String eventId, String userId, String comment) {
        this(null, eventId, userId, comment, null);
    }

    public Comment(String id, String eventId, String userId, String comment, Date createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.comment = comment;
        this.createdAt = createdAt;
    }
    
    public Comment(Comment comment) {
    	this.id = comment.id;
    	this.eventId = comment.eventId;
    	this.userId = comment.userId;
    	this.comment = comment.comment;
    	this.createdAt = comment.createdAt == null ? null : (Date) comment.createdAt.clone();
    }

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
