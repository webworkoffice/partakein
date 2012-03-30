package in.partake.controller.api.event;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.base.Util;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.Enrollment;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class ChangeEnrollmentCommentAPITest extends APIControllerTest {

    @Test
    public void testChangeEnrollmentComment() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll/changeComment");
        loginAs(proxy, EVENT_ENROLLED_USER_ID);

        Enrollment original = loadEnrollment(EVENT_ENROLLED_USER_ID, DEFAULT_EVENT_ID);

        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "comment", "new comment");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        Enrollment enrollment = loadEnrollment(EVENT_ENROLLED_USER_ID, DEFAULT_EVENT_ID);

        assertThat(enrollment.getId(), is(original.getId()));
        assertThat(enrollment.getModifiedAt(), is(original.getModifiedAt()));
        assertThat(enrollment.getComment(), is("new comment"));
    }

    @Test
    public void testWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll/changeComment");

        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "comment", "new comment");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testWithInvalidSession() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll/changeComment");
        loginAs(proxy, EVENT_ENROLLED_USER_ID);

        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "comment", "new comment");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

    @Test
    public void testWithTooLongComment() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll/changeComment");
        loginAs(proxy, EVENT_ENROLLED_USER_ID);

        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "comment", Util.randomString(1025));
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_COMMENT_TOOLONG);
    }

    @Test
    public void testWithLongComment() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll/changeComment");
        loginAs(proxy, EVENT_ENROLLED_USER_ID);

        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "comment", Util.randomString(1024));
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);
    }
}
