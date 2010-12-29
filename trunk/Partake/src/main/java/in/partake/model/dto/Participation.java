package in.partake.model.dto;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Date;

public class Participation extends PartakeModel<PartakeModel<?>> {
    private String userId;
    private String comment;
    private int priority;
    private ParticipationStatus status;
    private LastParticipationStatus lastStatus;
    private Date modifiedAt;

    // priority, modifiedAt 順に並べる comparator 
    public static Comparator<Participation> getPriorityBasedComparator() {
        return new Comparator<Participation>() {
            @Override
            public int compare(Participation lhs, Participation rhs) {
            	if (lhs == rhs) return 0;
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
    
    public Participation() {
    }
    
    public Participation(String userId, String comment, ParticipationStatus status, int priority, LastParticipationStatus lastStatus, Date modifiedAt) {
        this.userId = userId;
        this.comment = comment;
        this.status = status;
        this.priority = priority;
        this.lastStatus = lastStatus;
        this.modifiedAt = modifiedAt;
    }
    
    public Participation(Participation p) {
        try {
            Field[] fields = Participation.class.getDeclaredFields();
            for (Field field : fields) {
                field.set(this, field.get(p));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }        
    }
    
    public String getUserId() {
        return userId;
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
