package in.partake.service;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.fixture.CacheTestDataProvider;
import in.partake.model.fixture.EnrollmentTestDataProvider;
import in.partake.model.fixture.EventTestDataProvider;
import in.partake.model.fixture.OpenIDLinkageTestDataProvider;
import in.partake.model.fixture.TwitterLinkageTestDataProvider;
import in.partake.model.fixture.UserPreferenceTestDataProvider;
import in.partake.model.fixture.UserTestDataProvider;

import org.apache.log4j.Logger;

/**
 * test 用の関数が登録された
 * @author shinyak
 *
 */
public final class TestService extends PartakeService {
    private static TestService INSTANCE = new TestService();
    private static Logger LOGGER = Logger.getLogger(TestService.class);

    public static TestService get() {
        return INSTANCE;
    }

    private TestService() {
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
            new CacheTestDataProvider().createFixtures(con, factory);
            new EnrollmentTestDataProvider().createFixtures(con, factory);
            new EventTestDataProvider().createFixtures(con, factory);
            new OpenIDLinkageTestDataProvider().createFixtures(con, factory);
            new TwitterLinkageTestDataProvider().createFixtures(con, factory);
            new UserTestDataProvider().createFixtures(con, factory);
            new UserPreferenceTestDataProvider().createFixtures(con, factory);
            con.commit();
        } finally {
            con.invalidate();
        }
        // create lucene search index
        EventService.get().recreateEventIndex();
    }
}
