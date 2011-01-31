package in.partake.model.dto.pk;

import org.apache.commons.lang.ObjectUtils;

public class EnrollmentPK {
    private String userId;
    private String eventId;
    
    public EnrollmentPK() {
        
    }
    
    public EnrollmentPK(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EnrollmentPK)) { return false; }
        
        EnrollmentPK lhs = this;
        EnrollmentPK rhs = (EnrollmentPK) obj;

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
