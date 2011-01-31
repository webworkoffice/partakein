package in.partake.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EventReminderStatus extends PartakeModel<EventReminderStatus> {
    @Id
    private String eventId;
    @Column
    private Date sentDateOfBeforeDeadlineOneday;
    @Column
    private Date sentDateOfBeforeDeadlineHalfday;
    @Column
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

    @Override
    public Object getPrimaryKey() {
        return eventId;
    }
    
    // ----------------------------------------------------------------------
    // 

    // TODO: equals should be implemented.
    
    // ----------------------------------------------------------------------
    // 
    
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
