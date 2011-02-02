package in.partake.model.dto;

import in.partake.model.dto.auxiliary.LastParticipationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.pk.EnrollmentPK;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.apache.commons.lang.ObjectUtils;

@IdClass(EnrollmentPK.class)
@Entity(name = "Enrollments")
public class Enrollment extends PartakeModel<Enrollment> {
    @Id
    private String userId;
    @Id
    private String eventId;
    
    @Column
    private String comment;
    @Column
    private int priority;
    @Column
    private ParticipationStatus status;
    @Column
    private LastParticipationStatus lastStatus;
    @Column
    private Date modifiedAt;

    // priority, modifiedAt 順に並べる comparator 
    public static Comparator<Enrollment> getPriorityBasedComparator() {
        return new Comparator<Enrollment>() {
            @Override
            public int compare(Enrollment lhs, Enrollment rhs) {
            	if (lhs == rhs) { return 0; }
                if (lhs == null) { return -1; }
                if (rhs == null) { return 1; }

                if (lhs.getPriority() > rhs.getPriority()) { return -1; }
                if (lhs.getPriority() < rhs.getPriority()) { return 1; }
                int x = lhs.getModifiedAt().compareTo(rhs.getModifiedAt());
                if (x != 0) { return x; }
                return lhs.getUserId().compareTo(rhs.getUserId());
            }
        };        
    }
    
    // ----------------------------------------------------------------------
    // constructors
    
    public Enrollment() {
        this.priority = 0;
    }
    
    public Enrollment(String userId, String eventId, String comment, ParticipationStatus status, int priority, LastParticipationStatus lastStatus, Date modifiedAt) {
        this.userId = userId;
        this.eventId = eventId;
        this.comment = comment;
        this.status = status;
        this.priority = priority;
        this.lastStatus = lastStatus;
        this.modifiedAt = modifiedAt;
    }
    
    public Enrollment(Enrollment p) {
        this.userId = p.userId;
        this.eventId = p.eventId;
        this.comment = p.comment;
        this.status = p.status;
        this.priority = p.priority;
        this.lastStatus = p.lastStatus;
        this.modifiedAt = p.modifiedAt == null ? null : (Date) p.modifiedAt.clone();
    }

    @Override
    public Object getPrimaryKey() {
        return new EnrollmentPK(userId, eventId);
    }
    
    // ----------------------------------------------------------------------
    // equals method     
    
    public boolean equals(Object obj) {
        if (!(obj instanceof Enrollment)) { return false; }
        
        Enrollment lhs = this;
        Enrollment rhs = (Enrollment) obj;
        
        if (!ObjectUtils.equals(lhs.userId,     rhs.userId))     { return false; }
        if (!ObjectUtils.equals(lhs.eventId,    rhs.eventId))    { return false; }
        if (!ObjectUtils.equals(lhs.comment,    rhs.comment))    { return false; }
        if (!ObjectUtils.equals(lhs.priority,   rhs.priority))   { return false; }
        if (!ObjectUtils.equals(lhs.status,     rhs.status))     { return false; }
        if (!ObjectUtils.equals(lhs.lastStatus, rhs.lastStatus)) { return false; }
        if (!ObjectUtils.equals(lhs.modifiedAt, rhs.modifiedAt)) { return false; }
        
        return true;
    }
    
    public int hashCode() {
        int hashCode = 0;
        
        hashCode = hashCode * 37 + ObjectUtils.hashCode(userId);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(eventId);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(comment);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(priority);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(status);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(lastStatus);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(modifiedAt);
        
        return hashCode;
    }
    
    // ----------------------------------------------------------------------
    // 
    
    public String getUserId() {
        return userId;
    }
    
    public String getEventId() {
        return eventId;
    }

    public String getComment() {
        return comment;
    }
    
    public ParticipationStatus getStatus() {
        return status;
    }

    /**
     * 前回チェック時のステータス。ここは、 ENROLLED, NOT_ENROLLED のいずれかでなければならない。
     * 変更時に、この値が ENROLLED -> NOT_ENROLLED もしくあｈ NOT_ENROLLED -> ENROLLED になっていれば、
     * DM によって通知を出す。
     * @return
     */
    public LastParticipationStatus getLastStatus() {
        return lastStatus;
    }

    public int getPriority() {
        return priority;
    }
    
    public Date getModifiedAt() {
        return modifiedAt;
    }

	public void setUserId(String userId) {
		checkFrozen();
		this.userId = userId;
	}

	public void setComment(String comment) {
		checkFrozen();
		this.comment = comment;
	}

	public void setPriority(int priority) {
		checkFrozen();
		this.priority = priority;
	}

	public void setStatus(ParticipationStatus status) {
		checkFrozen();
		this.status = status;
	}

	public void setLastStatus(LastParticipationStatus lastStatus) {
		checkFrozen();
		this.lastStatus = lastStatus;
	}

	public void setModifiedAt(Date modifiedAt) {
		checkFrozen();
		this.modifiedAt = modifiedAt;
	}
}
