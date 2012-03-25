package in.partake.model.dao;

import java.util.Date;
import java.util.UUID;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IEventReminderAccess;
import in.partake.model.dto.EventReminder;

import org.junit.Before;

public class EventReminderAccessTest extends AbstractDaoTestCaseBase<IEventReminderAccess, EventReminder, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getEventReminderAccess());
    }

    @Override
    protected EventReminder create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("reminder" + pkSalt).hashCode());
        return new EventReminder(uuid.toString(), new Date(objNumber), null, null);
    }
}
