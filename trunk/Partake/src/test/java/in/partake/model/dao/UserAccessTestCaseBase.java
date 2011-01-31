package in.partake.model.dao;

import org.junit.Before;

public abstract class UserAccessTestCaseBase extends AbstractDaoTestCaseBase {
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getUserAccess());
    }
}
