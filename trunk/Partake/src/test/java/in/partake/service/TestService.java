package in.partake.service;

import java.util.Date;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.model.fixture.CacheTestDataProvider;
import in.partake.model.fixture.TwitterLinkageTestDataProvider;
import in.partake.model.fixture.UserTestDataProvider;

import org.apache.log4j.Logger;

/**
 * test 用の関数が登録された
 * @author shinyak
 *
 */
public final class TestService extends PartakeService {
    private static TestService instance = new TestService();
    private static Logger logger = Logger.getLogger(TestService.class);

    public static TestService get() {
        return instance;
    }
    
    private TestService() {
    }

    // ----------------------------------------------------------------------

    public void setDefaultFixtures() throws DAOException {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        try {
            con.beginTransaction();
            new CacheTestDataProvider().createFixtures(con, factory);
            new UserTestDataProvider().createFixtures(con, factory);
            new TwitterLinkageTestDataProvider().createFixtures(con, factory);
            con.commit();
        } finally {
            con.invalidate();
        }
    }    
}
