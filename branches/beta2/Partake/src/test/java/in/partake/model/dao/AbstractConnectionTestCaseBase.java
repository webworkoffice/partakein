package in.partake.model.dao;

import in.partake.base.TimeUtil;
import in.partake.resource.PartakeProperties;
import in.partake.service.DBService;
import in.partake.service.TestDatabaseService;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author shinyak
 *
 */
public abstract class AbstractConnectionTestCaseBase {    
    @BeforeClass
    public static void setUpOnce() {
        PartakeProperties.get().reset("unittest");
        TestDatabaseService.initialize();
    }
    
    // ------------------------------------------------------------

    protected void setup() throws DAOException {
        // remove the current data
        TimeUtil.resetCurrentDate();        
    }
    
    // ------------------------------------------------------------
    
    @Test
    public final void shouldAlwaysSucceed() {
        // do nothing
        // NOTE: this method ensures the setup method is called when no other test methods are defined. 
    }
    
    @Deprecated
    protected PartakeConnectionPool getPool() {
        return DBService.getPool();
    }
    
    @Deprecated
    protected PartakeDAOFactory getFactory() {
        return DBService.getFactory();
    }

}
