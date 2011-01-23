package in.partake.model;

import java.util.ArrayList;
import java.util.List;

import in.partake.model.dto.Participation;

/**
 * participation with related data.
 * @author shinyak
 *
 */
public class ParticipationEx extends Participation {
    private UserEx user;
    private List<String> relatedEventIds;
    
    public ParticipationEx(Participation p, UserEx user) {
        super(p);
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
