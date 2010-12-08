package in.partake.model.dto;

public class EventNotificationStatus {
	private String eventId;
	private boolean beforeDeadlineOneday;
	private boolean beforeDeadlineHalfday;
	private boolean beforeTheDay;

	public EventNotificationStatus() {
		this(null, false, false, false);
	}

	public EventNotificationStatus(String eventId, boolean beforeDeadlineOneday, boolean beforeDeadlineHalfday, boolean beforeTheDay) {
		this.eventId = eventId;
		this.beforeDeadlineOneday = beforeDeadlineOneday;
		this.beforeDeadlineHalfday = beforeDeadlineHalfday;
		this.beforeTheDay = beforeTheDay;
	}
	
	public String getEventId() {
		return eventId;
	}
	
    public boolean isBeforeDeadlineOneday() {
        return beforeDeadlineOneday;
    }	
	
    public boolean isBeforeDeadlineHalfday() {
        return beforeDeadlineHalfday;
    }

	public boolean isBeforeTheDay() {
		return beforeTheDay;
	}
	
	
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

    public void setBeforeDeadlineOneday(boolean beforeDeadlineOneday) {
        this.beforeDeadlineOneday = beforeDeadlineOneday;
    }

    public void setBeforeDeadlineHalfday(boolean beforeDeadlineHalfday) {
        this.beforeDeadlineHalfday = beforeDeadlineHalfday;
    }

	public void setBeforeTheDay(boolean beforeTheDay) {
		this.beforeTheDay = beforeTheDay;
	}
	
	
	
	
}
