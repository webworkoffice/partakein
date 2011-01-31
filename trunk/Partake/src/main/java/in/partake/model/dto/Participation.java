package in.partake.model.dto;

import in.partake.model.dto.auxiliary.LastParticipationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.pk.ParticipationPK;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@IdClass(ParticipationPK.class)
@Entity
public class Participation extends PartakeModel<Participation> {
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
    public static Comparator<Participation> getPriorityBasedComparator() {
        return new Comparator<Participation>() {
            @Override
            public int compare(Participation lhs, Participation rhs) {
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
    
    public Participation() {
        this.priority = 0;
    }
    
    public Participation(String userId, String eventId, String comment, ParticipationStatus status, int priority, LastParticipationStatus lastStatus, Date modifiedAt) {
        this.userId = userId;
        this.eventId = eventId;
        this.comment = comment;
        this.status = status;
        this.priority = priority;
        this.lastStatus = lastStatus;
        this.modifiedAt = modifiedAt;
    }
    
    public Participation(Participation p) {
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
        return new ParticipationPK(userId, eventId);
    }
    
    // ----------------------------------------------------------------------
    // equals method     
    
    // TODO: equals method should be implemented.
    
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
