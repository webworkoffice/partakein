package in.partake.model.dao;

import org.junit.After;
import org.junit.Test;

import in.partake.resource.PartakeProperties;
import in.partake.util.PDate;

/**
 * Dao のテストケースのベース。extends して、BeforeClass, AfterClass で
 * 利用する Connection の種類を設定のこと。
 * 
 * @author shinyak
 *
 */
public abstract class AbstractDaoTestCaseBase {
    private static PartakeDAOFactory factory;
    private static PartakeConnectionPool pool;
    
    static {
        reset();
    }
    
    protected static PartakeDAOFactory getFactory() {
        return factory;
    }
    
    protected static PartakeConnectionPool getPool() {
        return pool;
    }
    
    /**
     * PartakeService に必要なデータを読み直す。最初の初期化とユニットテスト用途のみを想定。
     */
    protected static void reset() {
        try {
            Class<?> factoryClass = Class.forName(PartakeProperties.get().getDAOFactoryClassName());
            factory = (PartakeDAOFactory) factoryClass.newInstance();
            
            Class<?> poolClass = Class.forName(PartakeProperties.get().getConnectionPoolClassName());
            pool = (PartakeConnectionPool) poolClass.newInstance();
            
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }        
    }
    
    @Test
    public final void shouldAlwaysSucceed() {
        // do nothing
        // NOTE: this method ensures the setup method is called when no other test methods are defined. 
    }
    
    @After
    public void tearDown() throws DAOException {
        
    }
    
    protected void setup(ITruncatable t) throws DAOException {
        // remove the current data
        PDate.resetCurrentDate();
        
        if (t != null) {
            // truncate all data.
            PartakeConnection con = pool.getConnection();
            try {
                con.beginTransaction();
                t.truncate(con);
                con.commit();
            } finally {
                con.invalidate();
            }
        }
    }
}
