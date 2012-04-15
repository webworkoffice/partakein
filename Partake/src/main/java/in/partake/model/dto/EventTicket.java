package in.partake.model.dto;

import in.partake.base.DateTime;
import in.partake.model.dto.auxiliary.TicketType;

import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

public class EventTicket extends PartakeModel<EventTicket> {
    private UUID id;
    private String eventId;
    private TicketType ticketType;
    private String name;
    private int amount; // The number of ticket. 0 means infinity.
    private DateTime createdAt;
    private DateTime modifiedAt;

    public EventTicket(UUID id, String eventId, String name, TicketType ticketType, int amount, DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.ticketType = ticketType;
        this.amount = amount;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public EventTicket(EventTicket t) {
        this(t.id, t.eventId, t.name, t.ticketType, t.amount, t.createdAt, t.modifiedAt);
    }

    public EventTicket(JSONObject obj) {
        this.id = UUID.fromString(obj.getString("id"));
        this.eventId = obj.getString("eventId");
        this.name = obj.getString("name");
        this.ticketType = TicketType.valueOf(obj.getString("ticketType"));
        this.amount = obj.getInt("amount");
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
        JSONObject json = new JSONObject();

        json.put("id", id.toString());
        json.put("eventId", eventId);
        json.put("name", name);
        json.put("ticketType", ticketType.toString());
        json.put("amount", amount);
        json.put("createdAt", createdAt.getTime());
        if (modifiedAt != null)
            json.put("modifiedAt", modifiedAt.getTime());

        return json;
    }

    // ----------------------------------------------------------------------
    // equals method

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EventTicket)) { return false; }

        EventTicket lhs = this;
        EventTicket rhs = (EventTicket) obj;

        if (!ObjectUtils.equals(lhs.id, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.eventId, rhs.eventId)) { return false; }
        if (!ObjectUtils.equals(lhs.name, rhs.name)) { return false; }
        if (!ObjectUtils.equals(lhs.ticketType, rhs.ticketType)) { return false; }
        if (!ObjectUtils.equals(lhs.amount, rhs.amount)) { return false; }
        if (!ObjectUtils.equals(lhs.createdAt, rhs.createdAt)) { return false; }
        if (!ObjectUtils.equals(lhs.modifiedAt, rhs.modifiedAt)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(name);
        code = code * 37 + ObjectUtils.hashCode(ticketType);
        code = code * 37 + ObjectUtils.hashCode(amount);
        code = code * 37 + ObjectUtils.hashCode(createdAt);
        code = code * 37 + ObjectUtils.hashCode(modifiedAt);

        return code;
    }


    // ----------------------------------------------------------------------
    // accessors

    public UUID getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public int getAmount() {
        return amount;
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

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setName(String name) {
        checkFrozen();
        this.name = name;
    }

    public void setTicketType(TicketType ticketType) {
        checkFrozen();
        this.ticketType = ticketType;
    }

    public void setAmount(int amount) {
        checkFrozen();
        this.amount = amount;
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
