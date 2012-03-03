package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.resource.Constants;
import in.partake.resource.PartakeProperties;
import in.partake.service.TestService;
import in.partake.service.UserService;
import in.partake.servlet.PartakeSession;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.struts2.StrutsTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;

// When a test class extends TestCase class, such a test class seems to run on JUnit3.
// To avoid it, @RunWith(JUnit4.class) is set.
@RunWith(JUnit4.class)
public abstract class AbstractPartakeControllerTest extends StrutsTestCase {

    @BeforeClass
    public static void setUpOnce() {
        // TODO: Should share the code with AbstractConnectionTestCaseBase.
        PartakeProperties.get().reset("unittest");

        try {
            if (PartakeProperties.get().getBoolean("in.partake.database.unittest_initialization"))
                initializeDataSource();
        } catch (NameAlreadyBoundException e) {
            // Maybe already DataSource is created.
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        TestService.initialize();
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

    // Make setUp called before each test. 
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    // Make tearDown called after each test. 
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * action proxy を取得し、
     *  1) session がなければ付与する。
     *  2) parameters がなければ付与する。
     */
    @Override
    protected ActionProxy getActionProxy(String uri) {
        ActionProxy proxy = super.getActionProxy(uri);
        if (proxy == null) { return null; }

        ActionContext actionContext = proxy.getInvocation().getInvocationContext();

        if (actionContext.getSession() == null) {
            Map<String, Object> session = new HashMap<String, Object>();
            actionContext.setSession(session);

            // Adds Partake session 
            session.put(Constants.ATTR_PARTAKE_SESSION, PartakeSession.createInitialPartakeSession());
        }

        if (actionContext.getParameters() == null) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            actionContext.setParameters(parameters);
        }

        return proxy;
    }

    /** log in した状態にする */
    protected void loginAs(ActionProxy proxy, String userId) throws DAOException {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();
        assert actionContext.getSession() != null;

        UserEx user = UserService.get().getUserExById(userId);
        if (user == null) {
            throw new RuntimeException("No such user.");
        }
        actionContext.getSession().put(Constants.ATTR_USER, user);
    }

    /** logout する */
    protected void logout(ActionProxy proxy) throws DAOException {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();
        assert actionContext.getSession() != null;

        actionContext.getSession().remove(Constants.ATTR_USER);
    }

    protected void addParameter(ActionProxy proxy, String key, Object obj) throws DAOException {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();        
        assert actionContext.getSession() != null;

        actionContext.getParameters().put(key, obj);
    }

    protected void addValidSessionTokenToParameter(ActionProxy proxy) throws DAOException {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();        
        assert actionContext.getSession() != null;

        PartakeSession session = (PartakeSession) actionContext.getSession().get(Constants.ATTR_PARTAKE_SESSION);
        actionContext.getParameters().put("sessionToken", session.getCSRFPrevention().getSessionToken());        
    }

    protected void addInvalidSessionTokenToParameter(ActionProxy proxy) throws DAOException {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();        
        actionContext.getParameters().put("sessionToken", "INVALID-SESSION-TOKEN");        
    }

    // ----------------------------------------------------------------------

    protected void assertResultSuccess(ActionProxy proxy) throws Exception {
        Assert.assertEquals(200, response.getStatus());
    }

    protected void assertResultInvalid(ActionProxy proxy) throws Exception {
        // Assert.assertEquals(400, response.getStatus());
        Assert.assertTrue(response.getRedirectedUrl().startsWith("/auth/invalid"));
    }

    protected void assertResultLoginRequired(ActionProxy proxy) throws Exception {
        // Will be redirected to /auth/loginRequired
        // status code should be 401.
        // Assert.assertEquals(401, response.getStatus());
        Assert.assertTrue(response.getRedirectedUrl().startsWith("/auth/loginRequired"));
    }

    protected void assertResultForbidden(ActionProxy proxy) throws Exception {
        // status code should be 403
        // Assert.assertEquals(403, response.getStatus());
        Assert.assertTrue(response.getRedirectedUrl().startsWith("/forbidden"));
    }

    protected void assertResultError(ActionProxy proxy) throws Exception {
        // Assert.assertEquals(500, response.getStatus());
        Assert.assertTrue(response.getRedirectedUrl().startsWith("/error"));
    }
}
