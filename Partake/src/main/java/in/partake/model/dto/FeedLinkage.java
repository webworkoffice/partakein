package in.partake.model.dto;

import org.apache.commons.lang.ObjectUtils;

public class FeedLinkage extends PartakeModel<FeedLinkage> {
    private String id;
    private String userId; 
    
    public FeedLinkage() {
        // default constructor.
    }
    
    public FeedLinkage(String id, String userId) {
        this.id = id;
        this.userId = userId;
    }
    
    public FeedLinkage(FeedLinkage linkage) {
        this.id = linkage.id;
        this.userId = linkage.userId;
    }

    // ----------------------------------------------------------------------
    // equal methods
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FeedLinkage)) { return false; }
        
        FeedLinkage lhs = this;
        FeedLinkage rhs = (FeedLinkage) obj;
        
        if (!ObjectUtils.equals(lhs.id, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.userId, rhs.userId)) { return false; }
        return true;
    }
    
    
    // ----------------------------------------------------------------------
    // accessors
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }
}
