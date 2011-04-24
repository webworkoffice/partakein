package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.resource.Constants;
import in.partake.service.TestService;
import in.partake.service.UserService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.StrutsTestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;

// When a test class extends TestCase class, such a test class seems to run on JUnit3.
// To avoid it, @RunWith(JUnit4.class) is set.
@RunWith(JUnit4.class)
public class PartakeControllerTestCase extends StrutsTestCase {

    public static void createFixtures() throws Exception {
        // testUser という id の user がいることを保証する。
        User user = new User("testUser", 1, new Date(), null);
        TwitterLinkage linkage = new TwitterLinkage(1, "testUser", "testUser", "accessToken", "accessTokenSecret", "http://www.example.com/", "testUser");
        
        TestService.get().createUser(user, linkage);
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
     * action proxy を取得し、session がなければ付与する。
     */
    @Override
    protected ActionProxy getActionProxy(String uri) {
        ActionProxy proxy = super.getActionProxy(uri);
        if (proxy == null) { return null; }

        ActionContext actionContext = proxy.getInvocation().getInvocationContext();
        if (actionContext.getSession() == null) {
            Map<String, Object> session = new HashMap<String, Object>();
            actionContext.setSession(session);
        }
        
        return proxy;
    }
    
    /** log in した状態にする */
    protected void login(ActionProxy proxy) throws DAOException {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();
        
        assert actionContext.getSession() != null;
        
        UserEx user = UserService.get().getUserExById("testUser");
        if (user == null) {
            throw new RuntimeException();
        }
        actionContext.getSession().put(Constants.ATTR_USER, user);
    }
}
