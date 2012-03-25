package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.ICalendarLinkageAccess;
import in.partake.model.dto.CalendarLinkage;

import java.util.UUID;

import org.junit.Before;

public class CalendarLinkageAccessTest extends AbstractDaoTestCaseBase<ICalendarLinkageAccess, CalendarLinkage, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getCalendarAccess());
    }

    @Override
    protected CalendarLinkage create(long pkNumber, String pkSalt, int objNumber) {
        UUID id = new UUID(pkNumber, ("calendarLinkage" + pkSalt).hashCode());
        return new CalendarLinkage(id.toString(), "calendarLinkage" + objNumber);
    }
}
