package in.partake.model.dao;

import java.util.UUID;

import in.partake.model.dao.access.ICalendarLinkageAccess;
import in.partake.model.dto.CalendarLinkage;
import in.partake.service.DBService;

import org.junit.Before;

public class CalendarLinkageAccessTest extends AbstractDaoTestCaseBase<ICalendarLinkageAccess, CalendarLinkage, String> {
    @Before
    public void setup() throws DAOException {
        super.setup(DBService.getFactory().getCalendarAccess());
    }
    
    @Override
    protected CalendarLinkage create(long pkNumber, String pkSalt, int objNumber) {
        UUID id = new UUID(pkNumber, ("calendarLinkage" + pkSalt).hashCode());
        return new CalendarLinkage(id.toString(), "calendarLinkage" + objNumber);
    }
}
