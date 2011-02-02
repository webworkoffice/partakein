package in.partake.model;

import java.util.ArrayList;
import java.util.List;

import in.partake.model.dto.Enrollment;

/**
 * enrollment with related data.
 * @author shinyak
 *
 */
public class EnrollmentEx extends Enrollment {
    private UserEx user;
    private List<String> relatedEventIds;
    
    public EnrollmentEx(Enrollment enrollment, UserEx user) {
        super(enrollment);
        this.user = user;
        this.relatedEventIds = new ArrayList<String>();
    }
    
    public UserEx getUser() {
        return this.user;
    }
    
    public List<String> getRelatedEventIds() {
        return relatedEventIds;
    }
    
    public void addRelatedEventId(String eventId) {
        checkFrozen();
        relatedEventIds.add(eventId);
    }
}
