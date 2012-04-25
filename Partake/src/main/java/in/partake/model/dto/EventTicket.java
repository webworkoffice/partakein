package in.partake.model.dto;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventTicketHolderList;
import in.partake.model.dto.auxiliary.TicketType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

public class EventTicket extends PartakeModel<EventTicket> {
    private UUID id;
    private String eventId;
    private TicketType ticketType;
    private String name;
    private int amount; // The number of ticket. 0 means infinity.

    private DateTime acceptsFrom; // From this time, ticket application starts.
    private DateTime acceptsTill; // Till this time, ticket application will continue.

    private DateTime createdAt;
    private DateTime modifiedAt;

    public static EventTicket createDefaultTicket(UUID id, Event event) {
        return new EventTicket(id, event.getId(), "自由席", TicketType.FREE_TICKET, 0, null, null, TimeUtil.getCurrentDateTime(), null);
    }

    public EventTicket(UUID id, String eventId, String name, TicketType ticketType, int amount,
            DateTime acceptsFrom, DateTime acceptsTill,
            DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.ticketType = ticketType;
        this.amount = amount;

        this.acceptsFrom = acceptsFrom;
        this.acceptsTill = acceptsTill;

        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public EventTicket(EventTicket t) {
        this(t.id, t.eventId, t.name, t.ticketType, t.amount, t.acceptsFrom, t.acceptsTill, t.createdAt, t.modifiedAt);
    }

    public EventTicket(JSONObject obj) {
        this.id = UUID.fromString(obj.getString("id"));
        this.eventId = obj.getString("eventId");
        this.name = obj.getString("name");
        this.ticketType = TicketType.valueOf(obj.getString("ticketType"));
        this.amount = obj.getInt("amount");

        if (obj.containsKey("acceptsFrom"))
            this.acceptsFrom = new DateTime(obj.getLong("acceptsFrom"));
        if (obj.containsKey("acceptsTill"))
            this.acceptsTill = new DateTime(obj.getLong("acceptsTill"));

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

        if (acceptsFrom != null)
            json.put("acceptsFrom", acceptsFrom.getTime());
        if (acceptsTill != null)
            json.put("acceptsTill", acceptsTill.getTime());

        json.put("createdAt", createdAt.getTime());
        if (modifiedAt != null)
            json.put("modifiedAt", modifiedAt.getTime());

        return json;
    }

    public JSONObject toSafeJSON() {
        // All fields seems not sensitive.
        JSONObject json = new JSONObject();

        json.put("id", id.toString());
        json.put("eventId", eventId);
        json.put("name", name);
        json.put("ticketType", ticketType.toString());
        json.put("amount", amount);

        if (acceptsFrom != null)
            json.put("acceptsFrom", acceptsFrom.getTime());
        if (acceptsTill != null)
            json.put("acceptsTill", acceptsTill.getTime());

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
        if (!ObjectUtils.equals(lhs.acceptsFrom, rhs.acceptsFrom)) { return false; }
        if (!ObjectUtils.equals(lhs.acceptsTill, rhs.acceptsTill)) { return false; }
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
        code = code * 37 + ObjectUtils.hashCode(acceptsFrom);
        code = code * 37 + ObjectUtils.hashCode(acceptsTill);
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

    public DateTime getAcceptsFrom() {
        return acceptsFrom;
    }

    public DateTime getAcceptsTill() {
        return acceptsTill;
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

    public void setAcceptsFrom(DateTime acceptsFrom) {
        checkFrozen();
        this.acceptsFrom = acceptsFrom;
    }

    public void setAcceptsTill(DateTime acceptsTill) {
        checkFrozen();
        this.acceptsTill = acceptsTill;
    }

    public void setCreatedAt(DateTime createdAt) {
        checkFrozen();
        this.createdAt = createdAt;
    }

    public void setModifiedAt(DateTime modifiedAt) {
        checkFrozen();
        this.modifiedAt = modifiedAt;
    }

    // ----------------------------------------------------------------------

    /**
     * true if reservation is acceptable now.
     * @return
     */
    public boolean canReserve(Event event) {
        DateTime rangeBegin = acceptsFrom != null ? acceptsFrom : new DateTime(0L);
        DateTime tmpRangeEnd = acceptsTill != null ? acceptsTill : event.getBeginDate();
        DateTime rangeEnd = TimeUtil.halfDayBefore(tmpRangeEnd);

        DateTime now = TimeUtil.getCurrentDateTime();

        return rangeBegin.isAfter(now) && now.isBefore(rangeEnd);
    }

    /**
     * get a calculated deadline. If deadline is set, it is returned. Otherwise, beginDate is deadline.
     * @return
     */
    public DateTime getCalculatedDeadline(Event event) {
        if (acceptsTill != null)
            return acceptsTill;

        return event.getBeginDate();
    }

    public DateTime getCalculatedReservationDeadline(Event event) {
        if (acceptsTill != null)
            return acceptsTill;

        return new DateTime(event.getBeginDate().getTime() - 1000 * 3600 * 3);
    }

    /**
     * true if all reservations are cancelled.
     * @return
     */
    public boolean isReservationTimeOver(Event event) {
        DateTime now = TimeUtil.getCurrentDateTime();
        DateTime deadline = getCalculatedReservationDeadline(event);

        return deadline.isBefore(now);
    }

    /**
     * From participations, distribute participation to enrolled, spare, or cancelled.
     * @param participations
     * @return
     */
    public EventTicketHolderList calculateParticipationList(Event event, List<EnrollmentEx> participations) {
        List<EnrollmentEx> enrolledParticipations = new ArrayList<EnrollmentEx>();
        List<EnrollmentEx> spareParticipations = new ArrayList<EnrollmentEx>();
        List<EnrollmentEx> cancelledParticipations = new ArrayList<EnrollmentEx>();
        boolean timeover = isReservationTimeOver(event);

        int reservedEnrolled = 0;
        int reservedSpare = 0;

        for (EnrollmentEx participation : participations) {
            switch (participation.getStatus()) {
            case CANCELLED:
                cancelledParticipations.add(participation);
                break;
            case ENROLLED:
                if (getAmount() == 0 || enrolledParticipations.size() < getAmount()) {
                    enrolledParticipations.add(participation);
                } else {
                    spareParticipations.add(participation);
                }
                break;
            case RESERVED:
                if (timeover) {
                    cancelledParticipations.add(participation);
                } else if (getAmount() == 0 || enrolledParticipations.size() < getAmount()) {
                    enrolledParticipations.add(participation);
                    ++reservedEnrolled;
                } else {
                    spareParticipations.add(participation);
                    ++reservedSpare;
                }
                break;
            case NOT_ENROLLED: // TODO: shouldn't happen.
                cancelledParticipations.add(participation);
                break;
            }
        }

        return new EventTicketHolderList(enrolledParticipations, spareParticipations, cancelledParticipations, reservedEnrolled, reservedSpare);
    }

}
