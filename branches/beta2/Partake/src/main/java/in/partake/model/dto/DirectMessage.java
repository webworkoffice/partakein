package in.partake.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

@Entity(name = "Messages")
public class DirectMessage extends PartakeModel<DirectMessage> {
    @Id
    private String id;
    @Column
    private String userId;		// TODO: senderId にすべきだな
    @Column @Lob
    private String message;
    @Column
    private String eventId;
    @Column @Temporal(TemporalType.TIMESTAMP)
    private Date   createdAt;

    public DirectMessage() {
        // do nothing
    }

    public DirectMessage(DirectMessage message) {
        this(message.id, message.userId, message.message, message.eventId, message.createdAt);
    }

    /** eventId が null -> system からのメッセージ、null でない -> user からのメッセージ。
     * TODO これは直感的でないので後で直すこと。
     */
    public DirectMessage(String id, String userId, String message, String eventId, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.eventId = eventId;
        this.createdAt = createdAt == null ? null : new Date(createdAt.getTime());
    }

    public DirectMessage(JSONObject obj) {
        this.id = obj.getString("id");
        this.userId = obj.optString("senderId", null);
        this.message = obj.getString("message");
        this.eventId = obj.optString("eventId", null);
        if (obj.containsKey("createdAt"))
            this.createdAt = new Date(obj.getLong("createdAt"));
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("senderId", userId);
        obj.put("message", message);
        obj.put("eventId", eventId);
        obj.put("createdAt", createdAt.getTime());
        return obj;
    }

    // ----------------------------------------------------------------------
    // equals method

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DirectMessage)) { return false; }

        DirectMessage lhs = this;
        DirectMessage rhs = (DirectMessage) obj;

        if (!(ObjectUtils.equals(lhs.id,        rhs.id)))        { return false; }
        if (!(ObjectUtils.equals(lhs.userId,    rhs.userId)))    { return false; }
        if (!(ObjectUtils.equals(lhs.message,   rhs.message)))   { return false; }
        if (!(ObjectUtils.equals(lhs.eventId,   rhs.eventId)))   { return false; }
        if (!(ObjectUtils.equals(lhs.createdAt, rhs.createdAt))) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(userId);
        code = code * 37 + ObjectUtils.hashCode(message);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(createdAt);

        return code;
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public Date getCreatedAt() {
        return createdAt == null ? null : new Date(createdAt.getTime());
    }

    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }

    public void setMessage(String message) {
        checkFrozen();
        this.message = message;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setCreatedAt(Date createdAt) {
        checkFrozen();
        this.createdAt = createdAt == null ? null : new Date(createdAt.getTime());
    }
}

