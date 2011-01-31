package in.partake.model.dto.pk;

import org.apache.commons.lang.ObjectUtils;

public class ParticipationPK {
    private String userId;
    private String eventId;
    
    public ParticipationPK() {
        
    }
    
    public ParticipationPK(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParticipationPK)) { return false; }
        
        ParticipationPK lhs = this;
        ParticipationPK rhs = (ParticipationPK) obj;

        if (!ObjectUtils.equals(lhs.userId, rhs.userId)) { return false; }
        if (!ObjectUtils.equals(lhs.eventId, rhs.eventId)) { return false; }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        
        code = code * 37 + ObjectUtils.hashCode(userId);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        
        return code;
    }
}
