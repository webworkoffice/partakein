package in.partake.model.dao;

import in.partake.model.dto.CalendarLinkage;
import org.junit.Before;

public abstract class CalendarLinkageAccessTestCaseBase extends AbstractDaoTestCaseBase<ICalendarLinkageAccess, CalendarLinkage, String> {
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getCalendarAccess());
    }
    
    @Override
    protected CalendarLinkage create(long pkNumber, String pkSalt, int objNumber) {
        return new CalendarLinkage(pkSalt + pkNumber, "userId" + objNumber);
    }
}
