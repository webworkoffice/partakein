package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.base.TimeUtil;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author shinyak
 *
 */
public abstract class AbstractConnectionTestCaseBase {
    @BeforeClass
    public static void setUpOnce() throws Exception {
        PartakeApp.initialize("unittest");
    }

    // ------------------------------------------------------------

    protected void setup() throws Exception {
        // remove the current data
        TimeUtil.resetCurrentDate();
    }

    // ------------------------------------------------------------

    @Test
    public final void shouldAlwaysSucceed() {
        // do nothing
        // NOTE: this method ensures the setup method is called when no other test methods are defined.
    }
}
