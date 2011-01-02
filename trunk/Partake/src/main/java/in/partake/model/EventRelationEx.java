package in.partake.model;

import in.partake.model.dto.EventRelation;

/**
 * event relation with related data.
 * @author shinyak
 *
 */
public class EventRelationEx extends EventRelation {
    private EventEx event;
    
    public EventRelationEx(EventRelation relation, EventEx event) {
        super(relation);
        this.event = event;
    }
    
    public EventEx getEvent() {
        return event;
    }
    
    public void setEvent(EventEx event) {
        checkFrozen();
        this.event = event;
    }
}
