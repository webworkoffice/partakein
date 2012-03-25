package in.partake.model.dto;

import in.partake.model.dto.pk.EventRelationPK;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.jdbc.Index;

@Entity(name = "EventRelations")
@IdClass(EventRelationPK.class)
public class EventRelation extends PartakeModel<EventRelation> {
    @Id @Index
    private String srcEventId;
    @Id @Index
    private String dstEventId;
    @Column
    private boolean required; // true if the original event requires this event.
    @Column
    private boolean priority; // true if the participants of the original event will be prioritized if participating this event.

    public EventRelation() {
        //
    }

    public EventRelation(EventRelation relation) {
        this(relation.srcEventId, relation.dstEventId, relation.required, relation.priority);
    }

    public EventRelation(String srcEventId, String dstEventId, boolean required, boolean priority) {
        this.srcEventId = srcEventId;
        this.dstEventId = dstEventId;
        this.required = required;
        this.priority = priority;
    }

    public EventRelation(JSONObject json) {
        this.srcEventId = json.getString("srcEventId");
        this.dstEventId = json.getString("dstEventId");
        this.required = json.getBoolean("required");
        this.priority = json.getBoolean("priority");
    }

    @Override
    public EventRelationPK getPrimaryKey() {
        return new EventRelationPK(srcEventId, dstEventId);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("srcEventId", srcEventId);
        obj.put("dstEventId", dstEventId);
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

        if (!(ObjectUtils.equals(lhs.srcEventId, rhs.srcEventId)))   { return false; }
        if (!(ObjectUtils.equals(lhs.dstEventId, rhs.dstEventId)))   { return false; }
        if (!(ObjectUtils.equals(lhs.required, rhs.required))) { return false; }
        if (!(ObjectUtils.equals(lhs.priority, rhs.priority))) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(srcEventId);
        code = code * 37 + ObjectUtils.hashCode(dstEventId);
        code = code * 37 + ObjectUtils.hashCode(required);
        code = code * 37 + ObjectUtils.hashCode(priority);

        return code;
    }


    // ----------------------------------------------------------------------
    // accessors

    public String getSrcEventId() {
        return srcEventId;
    }

    public String getDstEventId() {
        return dstEventId;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean hasPriority() {
        return priority;
    }

    public void setSrcEventId(String srcEventId) {
        checkFrozen();
        this.srcEventId = srcEventId;
    }

    public void setDstEventId(String dstEventId) {
        checkFrozen();
        this.dstEventId = dstEventId;
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
