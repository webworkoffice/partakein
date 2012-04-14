package in.partake.model.dto;

import java.util.UUID;

import in.partake.base.DateTime;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;


public class Message extends PartakeModel<Message> {
    private UUID id;

    private String subject;
    private String body;

    private String eventId;

    private DateTime createdAt;
    private DateTime modifiedAt;

    public Message() {
        // do nothing
    }

    public Message(Message message) {
        this(message.id, message.subject, message.body, message.eventId, message.createdAt, message.modifiedAt);
    }

    public Message(UUID id, String subject, String body, String eventId, DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.subject = subject;
        this.body = body;
        this.eventId = eventId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public Message(JSONObject obj) {
        this.id = UUID.fromString(obj.getString("id"));
        this.subject = obj.getString("subject");
        this.body = obj.getString("body");
        this.eventId = obj.optString("eventId");
        this.createdAt = new DateTime(obj.getLong("createdAt"));
        if (obj.containsKey("modifiedAt"))
            this.modifiedAt = new DateTime(obj.getLong("modifiedAt"));
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id.toString());
        obj.put("subject", subject);
        obj.put("body", body);
        if (eventId != null)
            obj.put("eventId", eventId);
        if (createdAt != null)
            obj.put("createdAt", createdAt.getTime());
        if (modifiedAt != null)
            obj.put("modifiedAt", modifiedAt.getTime());
        return obj;
    }

    // ----------------------------------------------------------------------
    // equals method

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Message)) { return false; }

        Message lhs = this;
        Message rhs = (Message) obj;

        if (!(ObjectUtils.equals(lhs.id,         rhs.id)))         { return false; }
        if (!(ObjectUtils.equals(lhs.subject,    rhs.subject)))    { return false; }
        if (!(ObjectUtils.equals(lhs.body,       rhs.body)))       { return false; }
        if (!(ObjectUtils.equals(lhs.eventId,    rhs.eventId)))    { return false; }
        if (!(ObjectUtils.equals(lhs.createdAt,  rhs.createdAt)))  { return false; }
        if (!(ObjectUtils.equals(lhs.modifiedAt, rhs.modifiedAt))) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(subject);
        code = code * 37 + ObjectUtils.hashCode(body);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(createdAt);
        code = code * 37 + ObjectUtils.hashCode(modifiedAt);

        return code;
    }

    // ----------------------------------------------------------------------
    // accessors

    public UUID getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getEventId() {
        return eventId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setId(UUID id) {
        checkFrozen();
        this.id = id;
    }

    public void setTitle(String title) {
        checkFrozen();
        this.subject = title;
    }

    public void setBody(String body) {
        checkFrozen();
        this.body = body;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setCreatedAt(DateTime createdAt) {
        checkFrozen();
        this.createdAt = createdAt;
    }

    public void setModifiedAt(DateTime modifiedAt) {
        checkFrozen();
        this.modifiedAt = modifiedAt;
    }
}

