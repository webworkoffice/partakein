package in.partake.service;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.fixture.PartakeTestDataProviderSet;
import in.partake.model.fixture.impl.EnrollmentTestDataProvider;
import in.partake.model.fixture.impl.EventTestDataProvider;
import in.partake.model.fixture.impl.OpenIDLinkageTestDataProvider;
import in.partake.model.fixture.impl.TwitterLinkageTestDataProvider;
import in.partake.model.fixture.impl.UserTestDataProvider;
import in.partake.resource.PartakeProperties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;

public class TestDatabaseService {
    private static final PartakeTestDataProviderSet testDataProviderSet = new PartakeTestDataProviderSet();    

    public static void initialize() {
        try {
            if (PartakeProperties.get().getBoolean("in.partake.database.unittest_initialization"))
                initializeDataSource();
        } catch (NameAlreadyBoundException e) {
            // Maybe already DataSource is created.
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        DBService.initialize();
    }
    
    public static PartakeTestDataProviderSet getTestDataProviderSet() {
        return testDataProviderSet;
    }

    /**
     * <p>test用のデータがDatastoreに存在することを保証する。作成されるデータは各Fixtureを参照。
     * @see CacheTestDataProvider
     * @see UserTestDataProvider
     * @see TwitterLinkageTestDataProvider
     * @see OpenIDLinkageTestDataProvider
     * @see EventTestDataProvider
     * @see EnrollmentTestDataProvider
     */
    public static void setDefaultFixtures() throws DAOException {
        // LOGGER.trace("TestService#setDefaultFixtures() is called, now start to create all fixtures.");
        PartakeConnection con = DBService.getPool().getConnection(); 
        PartakeDAOFactory factory = DBService.getFactory();
        try {
            con.beginTransaction();
            testDataProviderSet.createFixtures(con, factory);
            con.commit();
        } finally {
            con.invalidate();
        }
        
        // create lucene search index
        DeprecatedEventDAOFacade.get().recreateEventIndex();
    }

    private static void initializeDataSource() throws NamingException {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");

        InitialContext ic = new InitialContext();
        ic.createSubcontext("java:");
        ic.createSubcontext("java:/comp");
        ic.createSubcontext("java:/comp/env");
        ic.createSubcontext("java:/comp/env/jdbc");

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(PartakeProperties.get().getString("comp.env.jdbc.postgres.driver"));
        ds.setUrl(PartakeProperties.get().getString("comp.env.jdbc.postgres.url"));
        ds.setUsername(PartakeProperties.get().getString("comp.env.jdbc.postgres.user"));
        ds.setPassword(PartakeProperties.get().getString("comp.env.jdbc.postgres.password"));

        ic.bind("java:/comp/env/jdbc/postgres", ds);
    }

}
