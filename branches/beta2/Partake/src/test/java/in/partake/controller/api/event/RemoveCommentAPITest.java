package in.partake.controller.api.event;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;

public class RemoveCommentAPITest extends APIControllerTest {

    @Test
    public void testToRemoveOwnComment() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.COMMENT_ID1);
        addValidSessionTokenToParameter(proxy);

        loginAs(proxy, TestDataProvider.USER_ID1);

        proxy.execute();
        assertResultOK(proxy);        
    }
    
    @Test
    public void testToRemoveInvalidComment() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.COMMENT_INVALID_ID);
        
        loginAs(proxy, TestDataProvider.USER_ID1);

        proxy.execute();
        assertResultInvalid(proxy);
    }

    
    @Test
    public void testToRemoveCommentByEventOwner() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.COMMENT_ID2);
        addValidSessionTokenToParameter(proxy);

        loginAs(proxy, TestDataProvider.USER_ID1);

        proxy.execute();
        assertResultOK(proxy);
    }
    
    @Test
    public void testToRemoveCommentByEventEditor() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.COMMENT_ID2);
        addValidSessionTokenToParameter(proxy);

        loginAs(proxy, TestDataProvider.USER_ID2);

        proxy.execute();
        assertResultOK(proxy);        
    }
    
    @Test
    public void testToRemoveCommentByOtherOne() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.COMMENT_ID2);
        addValidSessionTokenToParameter(proxy);

        loginAs(proxy, TestDataProvider.USER_ID4);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testToRemoveCommentWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.COMMENT_ID2);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToRemoveCommentWithoutValidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.COMMENT_ID2);
        
        loginAs(proxy, TestDataProvider.USER_ID2);

        proxy.execute();
        assertResultInvalid(proxy);
    }

}
