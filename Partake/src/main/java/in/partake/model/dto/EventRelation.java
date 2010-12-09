package in.partake.model.dto;

public class EventRelation extends PartakeModel<EventRelation> {
	private String eventId;
	private boolean required; // true if the original event requires this event. 
	private boolean priority; // true if the participants of the original event will be prioritized if participating this event.  
	
	public EventRelation() {
		//
	}
	
	public EventRelation(String eventId, boolean required, boolean priority) {
		this.eventId = eventId;
		this.required = required;
		this.priority = priority;
	}
	
	// ----------------------------------------------------------------------
	
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
