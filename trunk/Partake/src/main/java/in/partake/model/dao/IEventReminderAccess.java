package in.partake.model.dao;

import in.partake.model.dto.EventReminder;

public interface IEventReminderAccess extends ITruncatable {
    public void updateEventReminderStatus(PartakeConnection con, EventReminder reminderStatus) throws DAOException;
    public EventReminder getEventReminderStatus(PartakeConnection con, String eventId) throws DAOException;
}
