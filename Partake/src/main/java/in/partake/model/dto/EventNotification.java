package in.partake.model.dto;

import in.partake.base.DateTime;
import in.partake.model.dto.auxiliary.NotificationType;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

/**
 * @author shinyak
 *
 */
// TODO: Should use MessageCode to convert EventNotification to Text.
public class EventNotification extends PartakeModel<EventNotification> {
    private String id;
    private String eventId;
    private List<String> userIds;
    private NotificationType notificationType;

    private DateTime createdAt;
    private DateTime modifiedAt;

    public EventNotification(EventNotification message) {
        this(message.id, message.eventId, message.userIds, message.notificationType, message.createdAt, message.modifiedAt);
    }

    public EventNotification(String id, String eventId, List<String> userIds, NotificationType notificationType,  DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.eventId = eventId;
        this.userIds = new ArrayList<String>(userIds);
        this.notificationType = notificationType;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public EventNotification(JSONObject obj) {
        this.id = obj.getString("id");
        this.eventId = obj.getString("eventId");

        this.userIds = new ArrayList<String>();
        JSONArray array = obj.getJSONArray("userIds");
        for (int i = 0; i < array.size(); ++i)
            userIds.add(array.getString(i));

        this.notificationType = NotificationType.safeValueOf(obj.getString("notificationType"));
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
        obj.put("id", id);
        obj.put("eventId", eventId);

        JSONArray array = new JSONArray();
        for (String userId : userIds)
            array.add(userId);

        obj.put("notificationType", notificationType.toString());
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
        if (!(ObjectUtils.equals(lhs.userIds,    rhs.userIds)))    { return false; }
        if (!(ObjectUtils.equals(lhs.notificationType,   rhs.notificationType)))   { return false; }
        if (!(ObjectUtils.equals(lhs.createdAt,  rhs.createdAt)))  { return false; }
        if (!(ObjectUtils.equals(lhs.modifiedAt, rhs.modifiedAt))) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(userIds);
        code = code * 37 + ObjectUtils.hashCode(notificationType);
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

    public List<String> getUserIds() {
        return this.userIds;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getModifiedAt() {
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

    public void setUserIds(List<String> userIds) {
        checkFrozen();
        this.userIds = new ArrayList<String>(userIds);
    }

    public void setNotificationType(NotificationType notificationType) {
        checkFrozen();
        this.notificationType = notificationType;
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

