package in.partake.controller.api.event;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.fixture.TestDataProvider;
import in.partake.resource.UserErrorCode;
import junit.framework.Assert;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class AttendanceAPITest extends APIControllerTest {

    @Test
    public void testShouldChangeToPresence() throws Exception {
        //
        {
            Enrollment enrollment = loadEnrollment(TestDataProvider.ATTENDANCE_ABSENT_USER_ID, TestDataProvider.DEFAULT_EVENT_TICKET_ID);
            Assert.assertEquals(AttendanceStatus.ABSENT, enrollment.getAttendanceStatus());
        }

        ActionProxy proxy = getActionProxy(API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_ABSENT_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        // Check status is changed.
        {
            Enrollment enrollment = loadEnrollment(TestDataProvider.ATTENDANCE_ABSENT_USER_ID, TestDataProvider.DEFAULT_EVENT_TICKET_ID);
            Assert.assertEquals(AttendanceStatus.PRESENT, enrollment.getAttendanceStatus());
        }
    }

    @Test
    public void testShouldChangeToAbsence() throws Exception {
        //
        {
            Enrollment enrollment = loadEnrollment(TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID, TestDataProvider.DEFAULT_EVENT_TICKET_ID);
            Assert.assertEquals(AttendanceStatus.UNKNOWN, enrollment.getAttendanceStatus());
        }

        ActionProxy proxy = getActionProxy(API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "absent");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        // Check status is changed.
        {
            Enrollment enrollment = loadEnrollment(TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID, TestDataProvider.DEFAULT_EVENT_TICKET_ID);
            Assert.assertEquals(AttendanceStatus.ABSENT, enrollment.getAttendanceStatus());
        }
    }

    @Test
    public void testShouldChangeToUnknown() throws Exception {
        //
        {
            Enrollment enrollment = loadEnrollment(TestDataProvider.ATTENDANCE_PRESENT_USER_ID, TestDataProvider.DEFAULT_EVENT_TICKET_ID);
            Assert.assertEquals(AttendanceStatus.PRESENT, enrollment.getAttendanceStatus());
        }

        ActionProxy proxy = getActionProxy(API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_PRESENT_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "unknown");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultOK(proxy);

        // Check status is changed.
        {
            Enrollment enrollment = loadEnrollment(TestDataProvider.ATTENDANCE_PRESENT_USER_ID, TestDataProvider.DEFAULT_EVENT_TICKET_ID);
            Assert.assertEquals(AttendanceStatus.UNKNOWN, enrollment.getAttendanceStatus());
        }
    }

    @Test
    public void testLoginRequired() throws Exception {
        ActionProxy proxy = getActionProxy(API_EVENT_ATTEND_URL);

        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testUserIdRequired() throws Exception {
        ActionProxy proxy = getActionProxy(API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        // addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_USER_ID);
    }

    @Test
    public void testEventIdRequired() throws Exception {
        ActionProxy proxy = getActionProxy(API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        // addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_EVENT_ID);
    }

    @Test
    public void testStatusRequired() throws Exception {
        ActionProxy proxy = getActionProxy(API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        // addParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.MISSING_ATTENDANCE_STATUS);
    }

    @Test
    public void testInvalidOwner() throws Exception {
        ActionProxy proxy = getActionProxy(API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_UNRELATED_USER_ID);

        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testInvalidArgument() throws Exception {
        ActionProxy proxy = getActionProxy(API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_PRESENT_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "hogehoge");
        addValidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_ATTENDANCE_STATUS);
    }

    @Test
    public void testInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy(API_EVENT_ATTEND_URL);
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);

        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "present");
        addInvalidSessionTokenToParameter(proxy);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

}
