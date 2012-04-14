package in.partake.model.dto;

import in.partake.base.TimeUtil;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;

import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

/**
 * Message を、「他に人に送る」ことを表現するクラス。
 *
 * @author shinyak
 *
 */

public class MessageEnvelope extends PartakeModel<MessageEnvelope> {
    private String id;
    private String receiverId;

    private String eventMessageId;
    private String userMessageId;
    private String eventNotificationId;

    private int numTried;
    private DirectMessagePostingType postingType;

    private Date lastTriedAt;
    private Date invalidAfter;
    private Date tryAfter;

    private Date createdAt;
    private Date modifiedAt;

    public static MessageEnvelope createForEventMessage(String receiverId, String eventMessageId, DirectMessagePostingType postingType) {
        MessageEnvelope envelope = new MessageEnvelope();
        envelope.receiverId = receiverId;
        envelope.eventMessageId = eventMessageId;
        envelope.numTried = 0;
        envelope.postingType = postingType;
        envelope.createdAt = TimeUtil.getCurrentDate();

        return envelope;
    }

    public MessageEnvelope() {
        // do nothing
    }

    public MessageEnvelope(String id, String receiverId,
            String eventMessageId, String userMessageId, String eventNotificationId,
            int numTried, DirectMessagePostingType postingType,
            Date lastTriedAt, Date invalidAfter, Date tryAfter, Date createdAt, Date modifiedAt) {
        this.id = id;
        this.receiverId = receiverId;
        this.eventMessageId = eventMessageId;
        this.userMessageId = userMessageId;
        this.eventNotificationId = eventNotificationId;
        this.numTried = numTried;
        this.postingType = postingType;
        this.lastTriedAt = lastTriedAt;
        this.invalidAfter = invalidAfter;
        this.tryAfter = tryAfter;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public MessageEnvelope(MessageEnvelope envelope) {
        this(envelope.id, envelope.receiverId,
                envelope.eventMessageId, envelope.userMessageId, envelope.eventNotificationId,
                envelope.numTried, envelope.postingType,
                envelope.lastTriedAt, envelope.invalidAfter, envelope.tryAfter, envelope.createdAt, envelope.modifiedAt);
    }

    public MessageEnvelope(JSONObject json) {
        this.id = json.getString("id");
        this.receiverId = json.getString("receiverId");
        if (json.containsKey("eventMessageId"))
            this.eventMessageId = json.getString("eventMessageId");
        if (json.containsKey("userMessageId"))
            this.userMessageId = json.getString("userMessageId");
        if (json.containsKey("eventNotificationId"))
            this.eventNotificationId = json.getString("eventNotificationId");
        this.numTried = json.getInt("numTried");
        this.postingType = DirectMessagePostingType.valueOf(json.getString("postingType"));

        if (json.containsKey("lastTriedAt"))
            this.lastTriedAt = new Date(json.getLong("lastTriedAt"));
        if (json.containsKey("invalidAfter"))
            this.invalidAfter = new Date(json.getLong("invalidAfter"));
        if (json.containsKey("tryAfter"))
            this.tryAfter = new Date(json.getLong("tryAfter"));
        if (json.containsKey("createdAt"))
            this.createdAt = new Date(json.getLong("createdAt"));
        if (json.containsKey("modifiedAt"))
            this.createdAt = new Date(json.getLong("modifiedAt"));
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        if (receiverId != null)
            obj.put("receiverId", receiverId);

        if (eventMessageId != null)
            obj.put("eventMessageId", eventMessageId);
        if (userMessageId != null)
            obj.put("userMessageId", userMessageId);
        if (eventNotificationId != null)
            obj.put("eventNotificationId", eventNotificationId);

        obj.put("numTried", numTried);
        obj.put("postingType", postingType.toString());
        if (lastTriedAt != null)
            obj.put("lastTriedAt", lastTriedAt.getTime());
        if (invalidAfter != null)
            obj.put("invalidAfter", invalidAfter.getTime());
        if (tryAfter != null)
            obj.put("tryAfter", tryAfter.getTime());

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
        if (!(obj instanceof MessageEnvelope)) { return false; }

        MessageEnvelope lhs = this;
        MessageEnvelope rhs = (MessageEnvelope) obj;

        if (!ObjectUtils.equals(lhs.id,  rhs.id))  { return false; }
        if (!ObjectUtils.equals(lhs.receiverId,  rhs.receiverId))  { return false; }

        if (!ObjectUtils.equals(lhs.eventMessageId,   rhs.eventMessageId))   { return false; }
        if (!ObjectUtils.equals(lhs.userMessageId,   rhs.userMessageId))   { return false; }
        if (!ObjectUtils.equals(lhs.eventNotificationId,   rhs.eventNotificationId))   { return false; }

        if (!ObjectUtils.equals(lhs.numTried,    rhs.numTried))    { return false; }
        if (!ObjectUtils.equals(lhs.postingType, rhs.postingType)) { return false; }

        if (!ObjectUtils.equals(lhs.lastTriedAt, rhs.lastTriedAt)) { return false; }
        if (!ObjectUtils.equals(lhs.invalidAfter,    rhs.invalidAfter))    { return false; }
        if (!ObjectUtils.equals(lhs.tryAfter,    rhs.tryAfter))    { return false; }

        if (!ObjectUtils.equals(lhs.createdAt,   rhs.createdAt))   { return false; }
        if (!ObjectUtils.equals(lhs.modifiedAt,   rhs.modifiedAt))   { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(receiverId);
        code = code * 37 + ObjectUtils.hashCode(eventMessageId);
        code = code * 37 + ObjectUtils.hashCode(userMessageId);
        code = code * 37 + ObjectUtils.hashCode(eventNotificationId);

        code = code * 37 + ObjectUtils.hashCode(numTried);
        code = code * 37 + ObjectUtils.hashCode(postingType);

        code = code * 37 + ObjectUtils.hashCode(lastTriedAt);
        code = code * 37 + ObjectUtils.hashCode(invalidAfter);
        code = code * 37 + ObjectUtils.hashCode(tryAfter);

        code = code * 37 + ObjectUtils.hashCode(createdAt);
        code = code * 37 + ObjectUtils.hashCode(modifiedAt);

        return code;
    }

    // ----------------------------------------------------------------------
    // accessors

    public String getEnvelopeId() { return id; }
    public String getReceiverId() { return receiverId; }
    public String getEventMessageId()  { return eventMessageId; }
    public String getUserMessageId()  { return userMessageId; }
    public String getEventNotificationId()  { return eventNotificationId; }

    public int getNumTried() { return numTried; }
    public DirectMessagePostingType getPostingType() { return postingType; }

    public Date getLastTriedAt() { return lastTriedAt; }
    public Date getDeadline() { return invalidAfter; }
    public Date getTryAfter() { return tryAfter; }

    public Date getCreatedAt() { return createdAt; }
    public Date getModifiedAt() { return modifiedAt; }

    public void updateForSendingFailure() {
        checkFrozen();
        this.numTried += 1;
        this.lastTriedAt = new Date();
    }

}
