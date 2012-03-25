package in.partake.controller.api.event;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class EnrollAPITest extends APIControllerTest {

    @Test
    public void testEnroll() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll");
        loginAs(proxy, DEFAULT_USER_ID);
        addParameter(proxy, "status", "enroll");
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "comment", "comment");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        Enrollment enrollment = loadEnrollment(DEFAULT_USER_ID, DEFAULT_EVENT_ID);
        assertThat(enrollment.getStatus(), is(ParticipationStatus.ENROLLED));
    }

    @Test
    public void testEnrollWontChangeEnrolledAt() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll");
        loginAs(proxy, EVENT_RESERVED_USER_ID);
        addParameter(proxy, "status", "enroll");
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "comment", "comment");
        addValidSessionTokenToParameter(proxy);

        Enrollment original = loadEnrollment(EVENT_RESERVED_USER_ID, DEFAULT_EVENT_ID);

        proxy.execute();
        assertResultOK(proxy);

        Enrollment enrollment = loadEnrollment(EVENT_RESERVED_USER_ID, DEFAULT_EVENT_ID);

        assertThat(enrollment.getStatus(), is(ParticipationStatus.ENROLLED));
        assertThat(enrollment.getModifiedAt(), is(original.getModifiedAt()));
    }

    @Test
    public void testWithoutValidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll");
        loginAs(proxy, DEFAULT_USER_ID);
        addParameter(proxy, "status", "enroll");
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "comment", "comment");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }
}
