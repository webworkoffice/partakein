package in.partake.model.fixture.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventReminderAccess;
import in.partake.model.dto.EventReminder;
import in.partake.model.fixture.TestDataProvider;

/**
 * BinaryData のテストデータを作成します。
 * @author shinyak
 *
 */
public class EventReminderTestDataProvider extends TestDataProvider<EventReminder> {
    @Override
    public EventReminder create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new EventReminder(uuid.toString(), null, null, new Date(objNumber));
    }

    @Override
    public List<EventReminder> createGetterSetterSamples() {
        List<EventReminder> array = new ArrayList<EventReminder>();
        array.add(new EventReminder("eventId", new Date(0L), new Date(0L), new Date(0L)));
        array.add(new EventReminder("eventId1", new Date(0L), new Date(0L), new Date(0L)));
        array.add(new EventReminder("eventId", new Date(1L), new Date(0L), new Date(0L)));
        array.add(new EventReminder("eventId", new Date(0L), new Date(1L), new Date(0L)));
        array.add(new EventReminder("eventId", new Date(0L), new Date(0L), new Date(1L)));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IEventReminderAccess dao = daos.getEventReminderAccess();
        dao.truncate(con);
    }
}
