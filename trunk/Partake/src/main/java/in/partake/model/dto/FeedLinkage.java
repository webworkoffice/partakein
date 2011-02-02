package in.partake.model.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.jdbc.Index;

@Entity(name = "FeedLinkages")
public class FeedLinkage extends PartakeModel<FeedLinkage> {
    @Id
    private String id;
    @Column @Index(unique = true)
    private String eventId; 
    
    public FeedLinkage() {
        // default constructor.
    }
    
    public FeedLinkage(String id, String eventId) {
        this.id = id;
        this.eventId = eventId;
    }
    
    public FeedLinkage(FeedLinkage linkage) {
        this.id = linkage.id;
        this.eventId = linkage.eventId;
    }
    
    @Override
    public Object getPrimaryKey() {
        return id;
    }

    // ----------------------------------------------------------------------
    // equal methods
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FeedLinkage)) { return false; }
        
        FeedLinkage lhs = this;
        FeedLinkage rhs = (FeedLinkage) obj;
        
        if (!ObjectUtils.equals(lhs.id, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.eventId, rhs.eventId)) { return false; }
        return true;
    }
    
    
    // ----------------------------------------------------------------------
    // accessors
    
    public String getId() {
        return id;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }
    
    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }
}
