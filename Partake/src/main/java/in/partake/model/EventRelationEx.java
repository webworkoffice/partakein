package in.partake.model;

import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventRelation;

/**
 * event relation with related data.
 * NOTE: EventEx をもつと無限ループしてしまう可能性があるので、Event に留める。
 * @author shinyak
 *
 */
public class EventRelationEx extends EventRelation {
    private Event event;

    public EventRelationEx(EventRelation relation, Event event) {
        super(relation);
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
