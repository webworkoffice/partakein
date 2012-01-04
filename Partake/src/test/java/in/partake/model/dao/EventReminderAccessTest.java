package in.partake.model.dao;

import java.util.Date;

import in.partake.model.dto.EventReminder;

import org.junit.Before;

public class EventReminderAccessTest extends AbstractDaoTestCaseBase<IEventReminderAccess, EventReminder, String> {
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getEventReminderAccess());
    }
    
    @Override
    protected EventReminder create(long pkNumber, String pkSalt, int objNumber) {
        return new EventReminder("eventId" + pkSalt + pkNumber, new Date(objNumber), null, null);
    }

}
