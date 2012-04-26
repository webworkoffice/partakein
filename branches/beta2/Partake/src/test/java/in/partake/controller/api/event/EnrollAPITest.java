package in.partake.controller.api.event;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.UserTicketApplication;
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

        UserTicketApplication enrollment = loadEnrollment(DEFAULT_USER_ID, DEFAULT_EVENT_TICKET_ID);
        assertThat(enrollment.getStatus(), is(ParticipationStatus.ENROLLED));
    }

    @Test
    public void testReserve() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll");
        loginAs(proxy, DEFAULT_USER_ID);
        addParameter(proxy, "status", "reserve");
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "comment", "comment");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        UserTicketApplication enrollment = loadEnrollment(DEFAULT_USER_ID, DEFAULT_EVENT_TICKET_ID);
        assertThat(enrollment.getStatus(), is(ParticipationStatus.RESERVED));
    }

    @Test
    public void testCancel() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll");
        loginAs(proxy, DEFAULT_USER_ID);
        addParameter(proxy, "status", "cancel");
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "comment", "comment");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        UserTicketApplication enrollment = loadEnrollment(DEFAULT_USER_ID, DEFAULT_EVENT_TICKET_ID);
        assertThat(enrollment.getStatus(), is(ParticipationStatus.CANCELLED));
    }

    @Test
    public void testWithInvalidStatus() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll");
        loginAs(proxy, DEFAULT_USER_ID);
        addParameter(proxy, "status", "invalid");
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "comment", "comment");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_ENROLL_STATUS);
    }

    @Test
    public void testEnrollWithoutComment() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll");
        loginAs(proxy, DEFAULT_USER_ID);
        addParameter(proxy, "status", "enroll");
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        UserTicketApplication enrollment = loadEnrollment(DEFAULT_USER_ID, DEFAULT_EVENT_TICKET_ID);
        assertThat(enrollment.getStatus(), is(ParticipationStatus.ENROLLED));
        assertThat(enrollment.getComment(), is(""));
    }

    @Test
    public void testEnrollWithLongComment() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll");
        loginAs(proxy, DEFAULT_USER_ID);
        addParameter(proxy, "status", "enroll");
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1025; ++i)
            builder.append('a');
        addParameter(proxy, "comment", builder.toString());
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_COMMENT_TOOLONG);
    }

    @Test
    public void testEnrollWontChangeEnrolledAt() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll");
        loginAs(proxy, EVENT_RESERVED_USER_ID);
        addParameter(proxy, "status", "enroll");
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "comment", "comment");
        addValidSessionTokenToParameter(proxy);

        UserTicketApplication original = loadEnrollment(EVENT_RESERVED_USER_ID, DEFAULT_EVENT_TICKET_ID);

        proxy.execute();
        assertResultOK(proxy);

        UserTicketApplication enrollment = loadEnrollment(EVENT_RESERVED_USER_ID, DEFAULT_EVENT_TICKET_ID);

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

    @Test
    public void testWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/enroll");
        addParameter(proxy, "status", "enroll");
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "comment", "comment");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }
}
