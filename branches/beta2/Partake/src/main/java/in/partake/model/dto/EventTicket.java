package in.partake.model.dto;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventTicketHolderList;
import in.partake.model.dto.auxiliary.TicketApplicationEnd;
import in.partake.model.dto.auxiliary.TicketApplicationStart;
import in.partake.model.dto.auxiliary.TicketPriceType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

public class EventTicket extends PartakeModel<EventTicket> {
    private UUID id;
    private String eventId;

    private String name;

    private TicketApplicationStart applicationStart;
    private int applicationStartDayBeforeEvent; // Only valid if applicationStart is nDayBefore.
    private DateTime customApplicationStartDate; // Only valid if applicationStart is custom.

    private TicketApplicationEnd applicationEnd;
    private int applicationEndDayBeforeEvent; // Only valid if applicationEnd is nDayBefore.
    private DateTime customApplicationEndDate; // Only valid if applicationEnd is custom.

    private TicketPriceType priceType;
    private int price;

    private boolean isAmountInfinite;
    private int amount;

    private DateTime createdAt;
    private DateTime modifiedAt;

    public static EventTicket createDefaultTicket(UUID id, String eventId) {
        return new EventTicket(id, eventId, "自由席",
                TicketApplicationStart.ANYTIME, 0, null,
                TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null,
                TicketPriceType.FREE, 0,
                true, 0,
                TimeUtil.getCurrentDateTime(), null);
    }

    public EventTicket(UUID id, String eventId, String name,
            TicketApplicationStart applicationStart, int applicationStartDayBeforeEvent, DateTime customApplicationStartDate,
            TicketApplicationEnd applicationEnd, int applicationEndDayBeforeEvent, DateTime customApplicationEndDate,
            TicketPriceType priceType, int price,
            boolean isAmountInfinite, int amount,
            DateTime createdAt, DateTime modifiedAt) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;

        this.applicationStart = applicationStart;
        this.applicationStartDayBeforeEvent = applicationStartDayBeforeEvent;
        this.customApplicationStartDate = customApplicationStartDate;

        this.applicationEnd = applicationEnd;
        this.applicationEndDayBeforeEvent = applicationEndDayBeforeEvent;
        this.customApplicationEndDate = customApplicationEndDate;

        this.priceType = priceType;
        this.price = price;

        this.isAmountInfinite = isAmountInfinite;
        this.amount = amount;

        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public EventTicket(EventTicket t) {
        this(t.id, t.eventId, t.name,
                t.applicationStart, t.applicationStartDayBeforeEvent, t.customApplicationStartDate,
                t.applicationEnd, t.applicationEndDayBeforeEvent, t.customApplicationEndDate,
                t.priceType, t.price,
                t.isAmountInfinite, t.amount,
                t.createdAt, t.modifiedAt);
    }

    public EventTicket(JSONObject obj) {
        this.id = UUID.fromString(obj.getString("id"));
        this.eventId = obj.getString("eventId");
        this.name = obj.getString("name");

        this.applicationStart = TicketApplicationStart.safeValueOf(obj.getString("applicationStart"));
        this.applicationStartDayBeforeEvent = obj.getInt("applicationStartDayBeforeEvent");
        if (obj.containsKey("customApplicationStartDate"))
            this.customApplicationStartDate = new DateTime(obj.getLong("customApplicationStartDate"));

        this.applicationEnd = TicketApplicationEnd.safeValueOf(obj.getString("applicationEnd"));
        this.applicationEndDayBeforeEvent = obj.getInt("applicationEndDayBeforeEvent");
        if (obj.containsKey("customApplicationEndDate"))
            this.customApplicationEndDate = new DateTime(obj.getLong("customApplicationEndDate"));

        this.priceType = TicketPriceType.valueOf(obj.getString("priceType"));
        this.price = obj.getInt("price");

        this.isAmountInfinite = obj.getBoolean("isAmountInfinite");
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

        json.put("applicationStart", applicationStart.toString());
        json.put("applicationStartDayBeforeEvent", applicationStartDayBeforeEvent);
        if (customApplicationStartDate != null)
            json.put("customApplicationStartDate", customApplicationStartDate.getTime());

        json.put("applicationEnd", applicationEnd.toString());
        json.put("applicationEndDayBeforeEvent", applicationEndDayBeforeEvent);
        if (customApplicationEndDate != null)
            json.put("customApplicationEndDate", customApplicationEndDate.getTime());

        json.put("priceType", priceType.toString());
        json.put("price", price);

        json.put("isAmountInfinite", isAmountInfinite);
        json.put("amount", amount);

        json.put("createdAt", createdAt.getTime());
        if (modifiedAt != null)
            json.put("modifiedAt", modifiedAt.getTime());

        return json;
    }

    public JSONObject toSafeJSON() {
        // Since all fields seems not sensitive, we call toJSON for now.
        return toJSON();
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
        if (!ObjectUtils.equals(lhs.applicationStart, rhs.applicationStart)) { return false; }
        if (!ObjectUtils.equals(lhs.applicationStartDayBeforeEvent, rhs.applicationStartDayBeforeEvent)) { return false; }
        if (!ObjectUtils.equals(lhs.customApplicationStartDate, rhs.customApplicationStartDate)) { return false; }
        if (!ObjectUtils.equals(lhs.applicationEnd, rhs.applicationEnd)) { return false; }
        if (!ObjectUtils.equals(lhs.applicationEndDayBeforeEvent, rhs.applicationEndDayBeforeEvent)) { return false; }
        if (!ObjectUtils.equals(lhs.customApplicationEndDate, rhs.customApplicationEndDate)) { return false; }
        if (!ObjectUtils.equals(lhs.priceType, rhs.priceType)) { return false; }
        if (!ObjectUtils.equals(lhs.price, rhs.price)) { return false; }
        if (!ObjectUtils.equals(lhs.isAmountInfinite, rhs.isAmountInfinite)) { return false; }
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
        code = code * 37 + ObjectUtils.hashCode(applicationStart);
        code = code * 37 + ObjectUtils.hashCode(applicationStartDayBeforeEvent);
        code = code * 37 + ObjectUtils.hashCode(applicationStartDayBeforeEvent);
        code = code * 37 + ObjectUtils.hashCode(applicationEnd);
        code = code * 37 + ObjectUtils.hashCode(applicationEndDayBeforeEvent);
        code = code * 37 + ObjectUtils.hashCode(customApplicationEndDate);
        code = code * 37 + ObjectUtils.hashCode(priceType);
        code = code * 37 + ObjectUtils.hashCode(price);
        code = code * 37 + ObjectUtils.hashCode(isAmountInfinite);
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

    public TicketApplicationStart getApplicationStart() {
        return applicationStart;
    }

    public int getApplicationStartDayBeforeEvent() {
        return applicationStartDayBeforeEvent;
    }

    public DateTime getCustomApplicationStartDate() {
        return customApplicationStartDate;
    }

    public TicketApplicationEnd getApplicationEnd() {
        return applicationEnd;
    }

    public int getApplicationEndDayBeforeEvent() {
        return applicationEndDayBeforeEvent;
    }

    public DateTime getCustomApplicationEndDate() {
        return customApplicationEndDate;
    }

    public TicketPriceType getPriceType() {
        return priceType;
    }

    public int getPrice() {
        return price;
    }

    public boolean isAmountInfinite() {
        return isAmountInfinite;
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

    public void setApplicationStart(TicketApplicationStart applicationStart) {
        checkFrozen();
        this.applicationStart = applicationStart;
    }

    public void setApplicationStartDayBeforeEvent(int applicationStartDayBeforeEvent) {
        checkFrozen();
        this.applicationStartDayBeforeEvent = applicationStartDayBeforeEvent;
    }

    public void setCustomApplicationStartDate(DateTime customApplicationStartDate) {
        checkFrozen();
        this.customApplicationStartDate = customApplicationStartDate;
    }

    public void setApplicationEnd(TicketApplicationEnd applicationEnd) {
        checkFrozen();
        this.applicationEnd = applicationEnd;
    }

    public void setApplicationEndDayBeforeEvent(int applicationEndDayBeforeEvent) {
        checkFrozen();
        this.applicationEndDayBeforeEvent = applicationEndDayBeforeEvent;
    }

    public void customApplicationEndDate(DateTime customApplicationEndDate) {
        checkFrozen();
        this.customApplicationEndDate = customApplicationEndDate;
    }

    public void setPriceType(TicketPriceType priceType) {
        checkFrozen();
        this.priceType = priceType;
    }

    public void setPrice(int price) {
        checkFrozen();
        this.price = price;
    }

    public void setAmountInfinite(boolean isAmountInfinite) {
        checkFrozen();
        this.isAmountInfinite = isAmountInfinite;
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

    // ----------------------------------------------------------------------

    public boolean acceptsApplication(Event event, DateTime now) {
        return acceptsFrom(event).isBefore(now) && now.isBefore(acceptsTill(event));
    }

    public DateTime acceptsFrom(Event event) {
        assert eventId.equals(event.getId());

        switch (applicationStart) {
        case ANYTIME:
            return new DateTime(0L);
        case FROM_NTH_DAY_BEFORE:
            return new DateTime(event.getBeginDate().getTime() - 24 * 3600 * 1000 * applicationStartDayBeforeEvent);
        case FROM_CUSTOM_DAY:
            return customApplicationStartDate;
        }

        assert false;
        throw new RuntimeException("should not happen.");
    }

    public DateTime acceptsTill(Event event) {
        switch (applicationEnd) {
        case TILL_TIME_BEFORE_EVENT:
            return event.getBeginDate();
        case TILL_TIME_AFTER_EVENT:
            if (event.hasEndDate())
                return event.getEndDate();
            else
                return event.getBeginDate();
        case TILL_NTH_DAY_BEFORE:
            return event.getBeginDate().nDayBefore(applicationEndDayBeforeEvent);
        case TILL_CUSTOM_DAY:
            return customApplicationEndDate;
        }

        assert false;
        throw new RuntimeException("should not happen.");
    }

    public DateTime acceptsReservationTill(Event event) {
        return acceptsTill(event).nDayBefore(1);
    }

    /**
     * true if reservation is acceptable now.
     * @return
     */
    public boolean canReserve(Event event) {
        DateTime now = TimeUtil.getCurrentDateTime();
        return acceptsFrom(event).isAfter(now) && now.isBefore(acceptsReservationTill(event));
    }

    /**
     * true if all reservations are cancelled.
     * @return
     */
    public boolean isReservationTimeOver(Event event) {
        DateTime now = TimeUtil.getCurrentDateTime();
        DateTime deadline = acceptsReservationTill(event);

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
