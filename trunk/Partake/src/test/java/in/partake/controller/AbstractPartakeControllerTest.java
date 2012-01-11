package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.resource.Constants;
import in.partake.resource.PartakeProperties;
import in.partake.service.PartakeService;
import in.partake.service.UserService;
import in.partake.servlet.PartakeSession;
import in.partake.util.PDate;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.StrutsTestCase;
import org.junit.AfterClass;
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
        PartakeProperties.get().reset("unittest");
        reset();
    }

    @AfterClass
    public static void tearDownOnce() {
        PartakeProperties.get().reset();
        reset();
    }

    /**
     * call this method and {@link PartakeProperties#reset(String)} at EachTestCase#setUpOnce() which is annotated as @BeforeClass.
     */
    protected static void reset() {
        PDate.resetCurrentDate();
        PartakeService.initialize();
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    @Before
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
}
