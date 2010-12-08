package in.partake.model.dto;

public class CalendarLinkage extends PartakeModel<CalendarLinkage> {
    private String id;
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
