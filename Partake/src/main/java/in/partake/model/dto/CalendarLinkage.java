package in.partake.model.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.jdbc.Unique;

@Entity(name = "CalendarLinkages")
public class CalendarLinkage extends PartakeModel<CalendarLinkage> {
    @Id
    private String id;
    @Column @Unique
    private String userId;
    
    public CalendarLinkage() {
        this(null, null);
    }
    
    public CalendarLinkage(String userId) {
        this(null, userId);
    }
    
    public CalendarLinkage(String id, String userId) {
        this.id = id;
        this.userId = userId;
    }
    
    public CalendarLinkage(CalendarLinkage src) {
        this.id = src.id;
        this.userId = src.userId;
    }
    
    @Override
    public Object getPrimaryKey() {
        return id;
    }
    
    // ----------------------------------------------------------------------
    // equals methods

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CalendarLinkage)) { return false; }
        
        CalendarLinkage lhs = this;
        CalendarLinkage rhs = (CalendarLinkage) obj;

        return ObjectUtils.equals(lhs.id, rhs.id) && ObjectUtils.equals(lhs.userId, rhs.userId);
    }
    
    @Override
    public int hashCode() {
        int x = id == null ? 0 : id.hashCode();
        int y = userId == null ? 0 : id.hashCode();
        
        return x * 37 + y;
    }
    
    // ----------------------------------------------------------------------
    // accessors
    
    public String getId() {
        return id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setId(String id) {
        checkFrozen();
        this.id = id;        
    }
    
    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }
}
