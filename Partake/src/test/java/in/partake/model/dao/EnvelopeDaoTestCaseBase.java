package in.partake.model.dao;

import org.junit.Before;

public abstract class EnvelopeDaoTestCaseBase extends AbstractDaoTestCaseBase {
    @Before
    public void setup() throws Exception {
        super.setup(getFactory().getEnvelopeAccess());
    }
}
