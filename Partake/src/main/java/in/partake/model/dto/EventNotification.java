package in.partake.model.dto;

import in.partake.model.dto.auxiliary.NotificationType;

import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

/**
 * @author shinyak
 *
 */
public class EventNotification extends PartakeModel<EventNotification> {
    private String id;
    private String eventId;
    private NotificationType notificationType;
    private String messageId;
    private Date createdAt;
    private Date modifiedAt;

    public EventNotification() {
        // do nothing
    }

    public EventNotification(EventNotification message) {
        this(message.id, message.eventId, message.notificationType, message.messageId, message.createdAt, message.modifiedAt);
    }

    public EventNotification(String id, String eventId, NotificationType notificationType, String messageId, Date createdAt, Date modifiedAt) {
        this.id = id;
        this.eventId = eventId;
        this.notificationType = notificationType;
        this.messageId = messageId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public EventNotification(JSONObject obj) {
        this.id = obj.getString("id");
        this.eventId = obj.getString("eventId");
        this.notificationType = NotificationType.safeValueOf(obj.getString("notificationType"));
        this.messageId = obj.getString("messageId");
        this.createdAt = new Date(obj.getLong("createdAt"));
        if (obj.containsKey("modifiedAt"))
            this.modifiedAt = new Date(obj.getLong("modifiedAt"));
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
        obj.put("notificationType", notificationType.toString());
        obj.put("messageId", messageId);
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
        if (!(obj instanceof EventNotification)) { return false; }

        EventNotification lhs = this;
        EventNotification rhs = (EventNotification) obj;

        if (!(ObjectUtils.equals(lhs.id,         rhs.id)))         { return false; }
        if (!(ObjectUtils.equals(lhs.eventId,    rhs.eventId)))    { return false; }
        if (!(ObjectUtils.equals(lhs.notificationType,   rhs.notificationType)))   { return false; }
        if (!(ObjectUtils.equals(lhs.messageId,  rhs.messageId)))  { return false; }
        if (!(ObjectUtils.equals(lhs.createdAt,  rhs.createdAt)))  { return false; }
        if (!(ObjectUtils.equals(lhs.modifiedAt, rhs.modifiedAt))) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(notificationType);
        code = code * 37 + ObjectUtils.hashCode(messageId);
        code = code * 37 + ObjectUtils.hashCode(createdAt);
        code = code * 37 + ObjectUtils.hashCode(modifiedAt);

        return code;
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public String getMessageId() {
        return messageId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setNotificationType(NotificationType notificationType) {
        checkFrozen();
        this.notificationType = notificationType;
    }

    public void setBody(String messageId) {
        checkFrozen();
        this.messageId = messageId;
    }

    public void setCreatedAt(Date createdAt) {
        checkFrozen();
        this.createdAt = createdAt != null ? new Date(createdAt.getTime()) : null;
    }

    public void setModifiedAt(Date modifiedAt) {
        checkFrozen();
        this.modifiedAt = modifiedAt != null ? new Date(modifiedAt.getTime()) : null;
    }
}

