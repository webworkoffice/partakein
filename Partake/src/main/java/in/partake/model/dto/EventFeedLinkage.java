package in.partake.model.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.jdbc.Index;


// TODO: Should be merged into Event...
@Entity(name = "EventFeedLinkages")
public class EventFeedLinkage extends PartakeModel<EventFeedLinkage> {
    @Id
    private String id;
    @Column @Index(unique = true)
    private String eventId;

    public EventFeedLinkage() {
        // default constructor.
    }

    public EventFeedLinkage(String id, String eventId) {
        this.id = id;
        this.eventId = eventId;
    }

    public EventFeedLinkage(EventFeedLinkage linkage) {
        this.id = linkage.id;
        this.eventId = linkage.eventId;
    }

    public EventFeedLinkage(JSONObject obj) {
        this.id = obj.getString("id");
        this.eventId = obj.getString("eventId");
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("eventId", eventId);
        return obj;
    }

    // ----------------------------------------------------------------------
    // equal methods

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EventFeedLinkage)) { return false; }

        EventFeedLinkage lhs = this;
        EventFeedLinkage rhs = (EventFeedLinkage) obj;

        if (!ObjectUtils.equals(lhs.id, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.eventId, rhs.eventId)) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(id);
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }
}
