package in.partake.model.dto.auxiliary;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

public class EventRelation {
    private String eventId;
    private boolean required; // true if the original event requires this event.
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

    public EventRelation(JSONObject json) {
        this.eventId = json.getString("eventId");
        this.required = json.getBoolean("required");
        this.priority = json.getBoolean("priority");
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("eventId", eventId);
        obj.put("required", required);
        obj.put("priority", priority);
        return obj;
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
        this.eventId = eventId;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }
}
