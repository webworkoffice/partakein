package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade;
import in.partake.resource.Constants;
import in.partake.resource.PartakeProperties;
import in.partake.service.TestDatabaseService;
import in.partake.session.PartakeSession;

import java.util.HashMap;
import java.util.Map;

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
        TestDatabaseService.initialize();
    }

    // Make setUp called before each test. 
    @Before
    public void setUp() throws Exception {
        super.setUp();
        TestDatabaseService.setDefaultFixtures();
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

        UserEx user = DeprecatedUserDAOFacade.get().getUserExById(userId);
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

    protected void assertLoggedOut(ActionProxy proxy) {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();
        assert actionContext.getSession() != null;

        Assert.assertTrue(actionContext.getSession() == null || !actionContext.getSession().containsKey(Constants.ATTR_USER));        
    }
    
    protected void assertRedirectedTo(String url) {
        Assert.assertEquals(url, response.getRedirectedUrl());
    }
    
    protected void assertResultSuccess(ActionProxy proxy) throws Exception {
        Assert.assertEquals(200, response.getStatus());
    }

    protected void assertResultInvalid(ActionProxy proxy) throws Exception {
        // Assert.assertEquals(400, response.getStatus());
        Assert.assertTrue(response.getRedirectedUrl().startsWith("/invalid"));
    }

    protected void assertResultLoginRequired(ActionProxy proxy) throws Exception {
        // Will be redirected to /auth/loginRequired
        // status code should be 401.
        // Assert.assertEquals(401, response.getStatus());
        Assert.assertTrue(response.getRedirectedUrl().startsWith("/loginRequired"));
    }

    protected void assertResultRedirect(ActionProxy proxy, String url) throws Exception {
        Assert.assertEquals(402, response.getStatus());
        if (url != null)
            Assert.assertEquals(url, response.getRedirectedUrl());
    }
    
    protected void assertResultForbidden(ActionProxy proxy) throws Exception {
        // status code should be 403
        // Assert.assertEquals(403, response.getStatus());
        Assert.assertTrue(response.getRedirectedUrl().startsWith("/prohibited"));
    }

    protected void assertResultNotFound(ActionProxy proxy) throws Exception {
        Assert.assertEquals(404, response.getStatus());
    }
    
    protected void assertResultError(ActionProxy proxy) throws Exception {
        // Assert.assertEquals(500, response.getStatus());
        Assert.assertTrue(response.getRedirectedUrl().startsWith("/error"));
    }
}
