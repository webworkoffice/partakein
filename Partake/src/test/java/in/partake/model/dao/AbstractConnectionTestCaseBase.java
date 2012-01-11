package in.partake.model.dao;

import in.partake.resource.PartakeProperties;
import in.partake.service.PartakeConnectionService;
import in.partake.util.PDate;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author shinyak
 *
 */
public abstract class AbstractConnectionTestCaseBase extends PartakeConnectionService {    
    @BeforeClass
    public static void setUpOnce() {
        PartakeProperties.get().reset("unittest");
        initializeConnectionPool();
    }

    // ------------------------------------------------------------

    protected void setup() throws DAOException {
        // remove the current data
        PDate.resetCurrentDate();        
    }
    
    // ------------------------------------------------------------
    
    @Test
    public final void shouldAlwaysSucceed() {
        // do nothing
        // NOTE: this method ensures the setup method is called when no other test methods are defined. 
    }
    
}
