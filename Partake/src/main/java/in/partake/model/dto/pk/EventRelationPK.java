package in.partake.model.dto.pk;

import org.apache.commons.lang.ObjectUtils;

public class EventRelationPK {
    private String srcEventId;
    private String dstEventId;
    
    public EventRelationPK() {
        
    }
    
    public EventRelationPK(String srcEventId, String dstEventId) {
        this.srcEventId = srcEventId;
        this.dstEventId = dstEventId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EventRelationPK)) { return false; }
        
        EventRelationPK lhs = this;
        EventRelationPK rhs = (EventRelationPK) obj;

        if (!ObjectUtils.equals(lhs.srcEventId, rhs.srcEventId)) { return false; }
        if (!ObjectUtils.equals(lhs.dstEventId, rhs.dstEventId)) { return false; }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        
        code = code * 37 + ObjectUtils.hashCode(srcEventId);
        code = code * 37 + ObjectUtils.hashCode(dstEventId);
        
        return code;
    }
}
