package in.partake.model.dao;

import org.junit.Before;

public class EventReminderDaoTestCaseBase extends AbstractDaoTestCaseBase {
    @Before
    public void setup() throws Exception {
        super.setup(getFactory().getEventReminderAccess());
    }

}
