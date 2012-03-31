package in.partake.model.fixture.impl;

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
    public static final byte[] BYTE1_CONTENT = new byte[] { 1, 2, 3 };

    @Override
    public EventReminder create() {
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public EventReminder create(long pkNumber, String pkSalt, int objNumber) {
        throw new RuntimeException("Not implementd yet");
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IEventReminderAccess dao = daos.getEventReminderAccess();
        dao.truncate(con);
    }
}
