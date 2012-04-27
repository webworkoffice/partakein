package in.partake.controller.api.event;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class RemoveCommentAPITest extends APIControllerTest {

    @Test
    public void testToRemoveOwnComment() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.COMMENTOR_COMMENT_ID);
        addValidSessionTokenToParameter(proxy);

        loginAs(proxy, TestDataProvider.EVENT_COMMENTOR_ID);

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testToRemoveInvalidComment() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.INVALID_COMMENT_ID);

        loginAs(proxy, TestDataProvider.EVENT_COMMENTOR_ID);

        proxy.execute();
        assertResultInvalid(proxy);
    }


    @Test
    public void testToRemoveCommentByEventOwner() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.COMMENTOR_COMMENT_ID);
        addValidSessionTokenToParameter(proxy);

        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testToRemoveCommentByEventEditor() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.COMMENTOR_COMMENT_ID);
        addValidSessionTokenToParameter(proxy);

        loginAs(proxy, TestDataProvider.EVENT_EDITOR_ID);

        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testToRemoveCommentByOtherOne() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.COMMENTOR_COMMENT_ID);
        addValidSessionTokenToParameter(proxy);

        loginAs(proxy, TestDataProvider.EVENT_UNRELATED_USER_ID);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testToRemoveCommentWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.COMMENTOR_COMMENT_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToRemoveCommentWithoutValidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/removeComment");
        addParameter(proxy, "commentId", TestDataProvider.COMMENTOR_COMMENT_ID);

        loginAs(proxy, TestDataProvider.EVENT_COMMENTOR_ID);

        proxy.execute();
        assertResultInvalid(proxy);
    }
}