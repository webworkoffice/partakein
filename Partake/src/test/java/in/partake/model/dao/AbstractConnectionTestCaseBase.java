package in.partake.model.dao;

import in.partake.base.TimeUtil;
import in.partake.resource.PartakeProperties;
import in.partake.service.DBService;
import in.partake.service.PartakeService;
import in.partake.service.TestDatabaseService;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author shinyak
 *
 */
public abstract class AbstractConnectionTestCaseBase {
    protected static PartakeConnectionPool pool;
    protected static PartakeDAOFactory factory;

    @BeforeClass
    public static void setUpOnce() {
        PartakeProperties.get().reset("unittest");
        PartakeService.initialize();
        TestDatabaseService.initialize();
        
        pool = DBService.getPool();
        factory = DBService.getFactory();
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
        return pool;
    }
    
    @Deprecated
    protected PartakeDAOFactory getFactory() {
        return factory;
    }
}
