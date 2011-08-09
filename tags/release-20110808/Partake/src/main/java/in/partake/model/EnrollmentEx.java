package in.partake.model;

import java.util.ArrayList;
import java.util.Comparator;
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
    private int priority;

    // priority, modifiedAt 順に並べる comparator 
    public static Comparator<EnrollmentEx> getPriorityBasedComparator() {
        return new Comparator<EnrollmentEx>() {
            @Override
            public int compare(EnrollmentEx lhs, EnrollmentEx rhs) {
                if (lhs == rhs) { return 0; }
                if (lhs == null) { return -1; }
                if (rhs == null) { return 1; }

                if ( lhs.isVIP() && !rhs.isVIP()) { return -1; } 
                if (!lhs.isVIP() &&  rhs.isVIP()) { return  1; } 
                
                if (lhs.getPriority() > rhs.getPriority()) { return -1; }
                if (lhs.getPriority() < rhs.getPriority()) { return 1; }
                int x = lhs.getModifiedAt().compareTo(rhs.getModifiedAt());
                if (x != 0) { return x; }
                return lhs.getUserId().compareTo(rhs.getUserId());
            }
        };        
    }

    
    public EnrollmentEx(Enrollment enrollment, UserEx user, int priority) {
        super(enrollment);
        this.user = user;
        this.relatedEventIds = new ArrayList<String>();
        this.priority = priority;
    }
    
    public UserEx getUser() {
        return this.user;
    }
    
    public List<String> getRelatedEventIds() {
        return relatedEventIds;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void addRelatedEventId(String eventId) {
        checkFrozen();
        relatedEventIds.add(eventId);
    }
    
    public void setPriority(int priority) {
        checkFrozen();
        this.priority = priority; 
    }
}
