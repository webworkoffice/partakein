package in.partake.model.dto;

import java.util.Date;

public class EventReminderStatus extends PartakeModel<EventReminderStatus> {
    private String eventId;
    private Date sentDateOfBeforeDeadlineOneday;
    private Date sentDateOfBeforeDeadlineHalfday;
    private Date sentDateOfBeforeTheDay;
    
    public EventReminderStatus() {
        this(null, null, null, null);
    }
    
    public EventReminderStatus(String eventId) {
        this(eventId, null, null, null);
    }
    
    public EventReminderStatus(String eventId, 
                    Date sentDateOfBeforeDeadlineOneday, Date sentDateOfBeforeDeadlineHalfday, Date sentDateOfBeforeTheDay) {
        this.eventId = eventId;
        this.sentDateOfBeforeDeadlineOneday = sentDateOfBeforeDeadlineOneday;
        this.sentDateOfBeforeDeadlineHalfday = sentDateOfBeforeDeadlineHalfday;
        this.sentDateOfBeforeTheDay = sentDateOfBeforeTheDay;
    }
    
    public EventReminderStatus(EventReminderStatus status) {
        this(status.eventId, status.sentDateOfBeforeDeadlineOneday, status.sentDateOfBeforeDeadlineHalfday, status.sentDateOfBeforeTheDay);
    }
    
    public String getEventId() {
        return eventId;
    }

    public Date getSentDateOfBeforeDeadlineOneday() {
        return sentDateOfBeforeDeadlineOneday;
    }

    public Date getSentDateOfBeforeDeadlineHalfday() {
        return sentDateOfBeforeDeadlineHalfday;
    }

    public Date getSentDateOfBeforeTheDay() {
        return sentDateOfBeforeTheDay;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setSentDateOfBeforeDeadlineOneday(Date sentDateOfBeforeDeadlineOneday) {
        checkFrozen();
        this.sentDateOfBeforeDeadlineOneday = sentDateOfBeforeDeadlineOneday;
    }

    public void setSentDateOfBeforeDeadlineHalfday(Date sentDateOfBeforeDeadlineHalfday) {
        checkFrozen();
        this.sentDateOfBeforeDeadlineHalfday = sentDateOfBeforeDeadlineHalfday;
    }

    public void setSentDateOfBeforeTheDay(Date sentDateOfBeforeTheDay) {
        checkFrozen();
        this.sentDateOfBeforeTheDay = sentDateOfBeforeTheDay;
    }
    
    
    
}
