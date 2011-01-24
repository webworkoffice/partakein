package in.partake.model.dao;

import in.partake.resource.PartakeProperties;


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
}
