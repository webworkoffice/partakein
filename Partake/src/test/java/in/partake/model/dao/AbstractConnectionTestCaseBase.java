package in.partake.model.dao;

import in.partake.resource.PartakeProperties;
import in.partake.service.PartakeService;
import in.partake.util.PDate;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author shinyak
 *
 */
public abstract class AbstractConnectionTestCaseBase extends PartakeService {    
    @BeforeClass
    public static void setUpOnce() {
        PartakeProperties.get().reset("unittest");
        
        try {
            if (PartakeProperties.get().getBoolean("in.partake.database.unittest_initialization"))
                initializeDataSource();
        } catch (NameAlreadyBoundException e) {
            // Maybe already DataSource is created.
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        
        initialize();
    }
    
    private static void initializeDataSource() throws NamingException {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");

        InitialContext ic = new InitialContext();
        ic.createSubcontext("java:");
        ic.createSubcontext("java:comp");
        ic.createSubcontext("java:comp/env");
        ic.createSubcontext("java:comp/env/jdbc");

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(PartakeProperties.get().getString("comp.env.jdbc.postgres.driver"));
        ds.setUrl(PartakeProperties.get().getString("comp.env.jdbc.postgres.url"));
        ds.setUsername(PartakeProperties.get().getString("comp.env.jdbc.postgres.user"));
        ds.setPassword(PartakeProperties.get().getString("comp.env.jdbc.postgres.password"));

        ic.bind("java:comp/env/jdbc/postgres", ds);
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
