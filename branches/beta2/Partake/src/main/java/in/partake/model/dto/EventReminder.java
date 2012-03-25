package in.partake.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

// TODO: EventReminder should be merged into Event.
@Entity(name = "EventReminders")
public class EventReminder extends PartakeModel<EventReminder> {
    @Id
    private String eventId;
    @Column
    private Date sentDateOfBeforeDeadlineOneday;
    @Column
    private Date sentDateOfBeforeDeadlineHalfday;
    @Column
    private Date sentDateOfBeforeTheDay;

    public EventReminder() {
        this(null, null, null, null);
    }

    public EventReminder(String eventId) {
        this(eventId, null, null, null);
    }

    public EventReminder(String eventId,
                    Date sentDateOfBeforeDeadlineOneday, Date sentDateOfBeforeDeadlineHalfday, Date sentDateOfBeforeTheDay) {
        this.eventId = eventId;
        this.sentDateOfBeforeDeadlineOneday = sentDateOfBeforeDeadlineOneday == null ? null : new Date(sentDateOfBeforeDeadlineOneday.getTime());
        this.sentDateOfBeforeDeadlineHalfday = sentDateOfBeforeDeadlineHalfday == null ? null : new Date(sentDateOfBeforeDeadlineHalfday.getTime());
        this.sentDateOfBeforeTheDay = sentDateOfBeforeTheDay == null ? null : new Date(sentDateOfBeforeTheDay.getTime());
    }

    public EventReminder(JSONObject obj) {
        this.eventId = obj.getString("id");
        if (obj.containsKey("sentDateOfBeforeDeadlineOneday"))
            sentDateOfBeforeDeadlineOneday = new Date(obj.getLong("sentDateOfBeforeDeadlineOneday"));
        if (obj.containsKey("sentDateOfBeforeDeadlineHalfday"))
            sentDateOfBeforeDeadlineHalfday = new Date(obj.getLong("sentDateOfBeforeDeadlineHalfday"));
        if (obj.containsKey("sentDateOfBeforeTheDay"))
            sentDateOfBeforeTheDay = new Date(obj.getLong("sentDateOfBeforeTheDay"));
    }

    public EventReminder(EventReminder status) {
        this(status.eventId, status.sentDateOfBeforeDeadlineOneday, status.sentDateOfBeforeDeadlineHalfday, status.sentDateOfBeforeTheDay);
    }

    @Override
    public Object getPrimaryKey() {
        return eventId;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", eventId);
        if (sentDateOfBeforeDeadlineOneday != null)
            obj.put("sentDateOfBeforeDeadlineOneday", sentDateOfBeforeDeadlineOneday.getTime());
        if (sentDateOfBeforeDeadlineHalfday != null)
            obj.put("sentDateOfBeforeDeadlineHalfday", sentDateOfBeforeDeadlineHalfday.getTime());
        if (sentDateOfBeforeTheDay != null)
            obj.put("sentDateOfBeforeTheDay", sentDateOfBeforeTheDay.getTime());
        return obj;
    }

    // ----------------------------------------------------------------------
    //

    // TODO: equals should be implemented.

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EventReminder)) { return false; }

        EventReminder lhs = this;
        EventReminder rhs = (EventReminder) obj;

        if (!ObjectUtils.equals(lhs.eventId, rhs.eventId)) { return false; }
        if (!ObjectUtils.equals(lhs.sentDateOfBeforeDeadlineOneday, rhs.sentDateOfBeforeDeadlineOneday)) { return false; }
        if (!ObjectUtils.equals(lhs.sentDateOfBeforeDeadlineHalfday, rhs.sentDateOfBeforeDeadlineHalfday)) { return false; }
        if (!ObjectUtils.equals(lhs.sentDateOfBeforeTheDay, rhs.sentDateOfBeforeTheDay)) { return false; }

        return true;
    }


    @Override
    public int hashCode() {
        int code = 0;

        code += code * 37 + ObjectUtils.hashCode(this.eventId);
        code += code * 37 + ObjectUtils.hashCode(this.sentDateOfBeforeDeadlineOneday);
        code += code * 37 + ObjectUtils.hashCode(this.sentDateOfBeforeDeadlineHalfday);
        code += code * 37 + ObjectUtils.hashCode(this.sentDateOfBeforeTheDay);

        return code;
    }


    // ----------------------------------------------------------------------
    //

    public String getEventId() {
        return eventId;
    }

    public Date getSentDateOfBeforeDeadlineOneday() {
        return sentDateOfBeforeDeadlineOneday == null ? null : new Date(sentDateOfBeforeDeadlineOneday.getTime());
    }

    public Date getSentDateOfBeforeDeadlineHalfday() {
        return sentDateOfBeforeDeadlineHalfday == null ? null : new Date(sentDateOfBeforeDeadlineHalfday.getTime());
    }

    public Date getSentDateOfBeforeTheDay() {
        return sentDateOfBeforeTheDay == null ? null : new Date(sentDateOfBeforeTheDay.getTime());
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setSentDateOfBeforeDeadlineOneday(Date sentDateOfBeforeDeadlineOneday) {
        checkFrozen();
        this.sentDateOfBeforeDeadlineOneday = sentDateOfBeforeDeadlineOneday == null ? null : new Date(sentDateOfBeforeDeadlineOneday.getTime());
    }

    public void setSentDateOfBeforeDeadlineHalfday(Date sentDateOfBeforeDeadlineHalfday) {
        checkFrozen();
        this.sentDateOfBeforeDeadlineHalfday = sentDateOfBeforeDeadlineHalfday == null ? null : new Date(sentDateOfBeforeDeadlineHalfday.getTime());
    }

    public void setSentDateOfBeforeTheDay(Date sentDateOfBeforeTheDay) {
        checkFrozen();
        this.sentDateOfBeforeTheDay = sentDateOfBeforeTheDay == null ? null : new Date(sentDateOfBeforeTheDay.getTime());
    }
}
