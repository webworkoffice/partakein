package in.partake.controller.api.event;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.resource.UserErrorCode;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class MakeAttendantVIPAPITest extends APIControllerTest {

    @Test
    public void testToMakeUserVip() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/makeAttendantVIP");
        loginAs(proxy, EVENT_OWNER_ID);

        assertThat(loadEnrollment(EVENT_ENROLLED_USER_ID, DEFAULT_EVENT_TICKET_ID).isVIP(), is(false));

        addParameter(proxy, "userId", EVENT_ENROLLED_USER_ID);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "vip", "true");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        assertThat(loadEnrollment(EVENT_ENROLLED_USER_ID, DEFAULT_EVENT_TICKET_ID).isVIP(), is(true));
    }

    @Test
    public void testToMakeUserNotVip() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/makeAttendantVIP");
        loginAs(proxy, EVENT_OWNER_ID);

        assertThat(loadEnrollment(EVENT_VIP_ENROLLED_USER_ID, DEFAULT_EVENT_TICKET_ID).isVIP(), is(true));

        addParameter(proxy, "userId", EVENT_VIP_ENROLLED_USER_ID);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "vip", "false");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        assertThat(loadEnrollment(EVENT_VIP_ENROLLED_USER_ID, DEFAULT_EVENT_TICKET_ID).isVIP(), is(false));
    }

    @Test
    public void testToMakeUserVipWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/makeAttendantVIP");

        addParameter(proxy, "userId", EVENT_ENROLLED_USER_ID);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "vip", "true");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToMakeUserVipByEditor() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/makeAttendantVIP");
        loginAs(proxy, EVENT_EDITOR_ID);

        assertThat(loadEnrollment(EVENT_ENROLLED_USER_ID, DEFAULT_EVENT_TICKET_ID).isVIP(), is(false));

        addParameter(proxy, "userId", EVENT_ENROLLED_USER_ID);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "vip", "true");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        assertThat(loadEnrollment(EVENT_ENROLLED_USER_ID, DEFAULT_EVENT_TICKET_ID).isVIP(), is(true));
    }

    @Test
    public void testToMakeUserVipByUnrelatedUser() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/makeAttendantVIP");
        loginAs(proxy, EVENT_UNRELATED_USER_ID);

        addParameter(proxy, "userId", EVENT_ENROLLED_USER_ID);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "vip", "true");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testToMakeUserVipWithoutUserId() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/makeAttendantVIP");
        loginAs(proxy, EVENT_OWNER_ID);

        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "vip", "true");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_USER_ID);
    }

    @Test
    public void testToMakeUnrelatedUserVip() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/makeAttendantVIP");
        loginAs(proxy, EVENT_OWNER_ID);

        addParameter(proxy, "userId", EVENT_UNRELATED_USER_ID);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "vip", "true");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_ATTENDANT_EDIT);
    }

    @Test
    public void testToMakeUserVipWithInvalidEventId() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/makeAttendantVIP");
        loginAs(proxy, EVENT_OWNER_ID);

        addParameter(proxy, "userId", EVENT_ENROLLED_USER_ID);
        addParameter(proxy, "eventId", INVALID_EVENT_ID);
        addParameter(proxy, "vip", "true");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_EVENT_ID);
    }

    @Test
    public void testToMakeUserVipWithoutEventId() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/makeAttendantVIP");
        loginAs(proxy, EVENT_OWNER_ID);

        addParameter(proxy, "userId", EVENT_ENROLLED_USER_ID);
        addParameter(proxy, "vip", "true");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_EVENT_ID);
    }

    @Test
    public void testToMakeUserVipWithoutVip() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/makeAttendantVIP");
        loginAs(proxy, EVENT_OWNER_ID);

        assertThat(loadEnrollment(EVENT_ENROLLED_USER_ID, DEFAULT_EVENT_TICKET_ID).isVIP(), is(false));

        addParameter(proxy, "userId", EVENT_ENROLLED_USER_ID);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_MISSING_VIP);
    }

    @Test
    public void testToMakeUserWithoutValidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/makeAttendantVIP");
        loginAs(proxy, EVENT_OWNER_ID);

        addParameter(proxy, "userId", EVENT_ENROLLED_USER_ID);
        addParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addParameter(proxy, "vip", "true");

        proxy.execute();

        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }
}
