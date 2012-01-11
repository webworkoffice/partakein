package in.partake.model.dao;

import in.partake.service.PartakeConnectionService;
import in.partake.util.PDate;

import org.junit.After;
import org.junit.Test;

/**
 * @author shinyak
 *
 */
public abstract class AbstractConnectionTestCaseBase extends PartakeConnectionService {
    static {
        reset();
    }
    
    /**
     * PartakeService に必要なデータを読み直す。最初の初期化とユニットテスト用途のみを想定。
     */
    protected static void reset() {
        PartakeConnectionService.initialize();
    }
    
    // ------------------------------------------------------------
    
    @After
    public void tearDown() throws DAOException {
        
    }
    
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
