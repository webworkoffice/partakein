package in.partake.model.dto;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.jdbc.Index;

@Entity(name = "EventRelations")
public class EventRelation extends PartakeModel<EventRelation> {
    @Column @Index
	private String eventId;
    @Column
	private boolean required; // true if the original event requires this event.
    @Column
	private boolean priority; // true if the participants of the original event will be prioritized if participating this event.  
	
	public EventRelation() {
		//
	}
	
	public EventRelation(EventRelation relation) {
	    this(relation.eventId, relation.required, relation.priority);
	}

    public EventRelation(String eventId, boolean required, boolean priority) {
        this.eventId = eventId;
        this.required = required;
        this.priority = priority;
    }
    
    @Override
    public Object getPrimaryKey() {
        return eventId;
    }
    
    @Override
    public EventRelation copy() {
        return new EventRelation(this);
    }

    // ----------------------------------------------------------------------
    // equals methods
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EventRelation)) { return false; }
        
        EventRelation lhs = this;
        EventRelation rhs = (EventRelation) obj;
        
        if (!(ObjectUtils.equals(lhs.eventId, rhs.eventId)))   { return false; }
        if (!(ObjectUtils.equals(lhs.required, rhs.required))) { return false; }
        if (!(ObjectUtils.equals(lhs.priority, rhs.priority))) { return false; }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(required);
        code = code * 37 + ObjectUtils.hashCode(priority);
        
        return code;
    }

    
    // ----------------------------------------------------------------------
    // accessors
	
	public String getEventId() {
		return eventId;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public boolean hasPriority() {
		return priority;
	}
	
	public void setEventId(String eventId) {
		checkFrozen();
		this.eventId = eventId;
	}
	
	public void setRequired(boolean required) {
		checkFrozen();
		this.required = required;
	}
	
	public void setPriority(boolean priority) {
		checkFrozen();
		this.priority = priority;
	}
}
