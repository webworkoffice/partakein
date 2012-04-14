package in.partake.model.fixture.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.ICalendarLinkageAccess;
import in.partake.model.dto.CalendarLinkage;
import in.partake.model.fixture.TestDataProvider;

/**
 *
 * @author shinyak
 *
 */
public class CalendarLinkageTestDataProvider extends TestDataProvider<CalendarLinkage> {
    @Override
    public CalendarLinkage create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new CalendarLinkage(uuid.toString(), "" + objNumber);
    }

    @Override
    public List<CalendarLinkage> createGetterSetterSamples() {
        List<CalendarLinkage> list = new ArrayList<CalendarLinkage>();

        list.add(new CalendarLinkage("id", "userId"));
        list.add(new CalendarLinkage("id1", "userId"));
        list.add(new CalendarLinkage("id", "userId2"));

        return list;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        ICalendarLinkageAccess dao = daos.getCalendarAccess();
        dao.truncate(con);

        dao.put(con, new CalendarLinkage(DEFAULT_CALENDAR_ID, DEFAULT_USER_ID));
        dao.put(con, new CalendarLinkage(ENROLLED_USER_CALENDAR_ID, EVENT_ENROLLED_USER_ID));
    }
}
