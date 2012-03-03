package in.partake.service;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.fixture.PartakeTestDataProviderSet;
import in.partake.model.fixture.impl.CacheTestDataProvider;
import in.partake.model.fixture.impl.EnrollmentTestDataProvider;
import in.partake.model.fixture.impl.EventTestDataProvider;
import in.partake.model.fixture.impl.OpenIDLinkageTestDataProvider;
import in.partake.model.fixture.impl.TwitterLinkageTestDataProvider;
import in.partake.model.fixture.impl.UserTestDataProvider;

import org.apache.log4j.Logger;

/**
 * test 用の関数が登録された
 * @author shinyak
 *
 */
public class TestService extends PartakeService {
    private static TestService INSTANCE = new TestService();
    private static Logger LOGGER = Logger.getLogger(TestService.class);
    
    protected final PartakeTestDataProviderSet testDataProviderSet;
    
    public static TestService get() {
        return INSTANCE;
    }

    protected TestService() {
        this.testDataProviderSet = new PartakeTestDataProviderSet();
    }
    
    public PartakeTestDataProviderSet getTestDataProviderSet() {
        return testDataProviderSet;
    }

    // ----------------------------------------------------------------------

    /**
     * <p>test用のデータがDatastoreに存在することを保証する。作成されるデータは各Fixtureを参照。
     * @see CacheTestDataProvider
     * @see UserTestDataProvider
     * @see TwitterLinkageTestDataProvider
     * @see OpenIDLinkageTestDataProvider
     * @see EventTestDataProvider
     * @see EnrollmentTestDataProvider
     */
    public void setDefaultFixtures() throws DAOException {
        LOGGER.trace("TestService#setDefaultFixtures() is called, now start to create all fixtures.");
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        try {
            con.beginTransaction();
            testDataProviderSet.createFixtures(con, factory);
            con.commit();
        } finally {
            con.invalidate();
        }
        
        // create lucene search index
        EventService.get().recreateEventIndex();
    }
}
