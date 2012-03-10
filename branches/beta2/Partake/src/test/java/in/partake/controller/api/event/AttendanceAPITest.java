package in.partake.controller.api.event;

import junit.framework.Assert;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class AttendanceAPITest extends APIControllerTest {

    @Test
    public void testShouldChangeToPresence() throws Exception {
        // 
        {
            Enrollment enrollment = DeprecatedEventDAOFacade.get().findEnrollment(TestDataProvider.DEFAULT_EVENT_ID, TestDataProvider.EVENT_ENROLLED_USER_ID);
            Assert.assertEquals(AttendanceStatus.ABSENT, enrollment.getAttendanceStatus()); 
        }
        
        ActionProxy proxy = getActionProxy("/api/event/attend");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);
        
        addParameter(proxy, "userId", TestDataProvider.EVENT_ENROLLED_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultOK(proxy);
        
        // Check status is changed.
        {
            Enrollment enrollment = DeprecatedEventDAOFacade.get().findEnrollment(TestDataProvider.DEFAULT_EVENT_ID, TestDataProvider.EVENT_ENROLLED_USER_ID);
            Assert.assertEquals(AttendanceStatus.PRESENT, enrollment.getAttendanceStatus());
        }
    }
    
    @Test
    public void testShouldChangeToAbsence() throws Exception {
        // 
        {
            Enrollment enrollment = DeprecatedEventDAOFacade.get().findEnrollment(TestDataProvider.DEFAULT_EVENT_ID, TestDataProvider.EVENT_ENROLLED_USER_ID);
            Assert.assertEquals(AttendanceStatus.UNKNOWN, enrollment.getAttendanceStatus()); 
        }
        
        ActionProxy proxy = getActionProxy("/api/event/attend");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);
        
        addParameter(proxy, "userId", TestDataProvider.EVENT_ENROLLED_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "absent");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultOK(proxy);
        
        // Check status is changed.
        {
            Enrollment enrollment = DeprecatedEventDAOFacade.get().findEnrollment(TestDataProvider.DEFAULT_EVENT_ID, TestDataProvider.EVENT_ENROLLED_USER_ID);
            Assert.assertEquals(AttendanceStatus.ABSENT, enrollment.getAttendanceStatus());
        }        
    }
    
    @Test
    public void testShouldChangeToUnknown() throws Exception {
        // 
        {
            Enrollment enrollment = DeprecatedEventDAOFacade.get().findEnrollment(TestDataProvider.DEFAULT_EVENT_ID, TestDataProvider.EVENT_ENROLLED_USER_ID);
            Assert.assertEquals(AttendanceStatus.PRESENT, enrollment.getAttendanceStatus()); 
        }
        
        ActionProxy proxy = getActionProxy("/api/event/attend");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);
        
        addParameter(proxy, "userId", TestDataProvider.EVENT_ENROLLED_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "unknown");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultOK(proxy);
        
        // Check status is changed.
        {
            Enrollment enrollment = DeprecatedEventDAOFacade.get().findEnrollment(TestDataProvider.DEFAULT_EVENT_ID, TestDataProvider.EVENT_ENROLLED_USER_ID);
            Assert.assertEquals(AttendanceStatus.UNKNOWN, enrollment.getAttendanceStatus());
        }        

    }
    
    @Test
    public void testLoginRequired() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/attend");
        
        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testUserIdRequired() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/attend");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);
        
        // addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testEventIdRequired() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/attend");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);
        
        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        // addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testStatusRequired() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/attend");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);
        
        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        // addParameter(proxy, "status", "present");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testInvalidOwner() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/attend");
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
        ActionProxy proxy = getActionProxy("/api/event/attend");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);
        
        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_PRESENT_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "hogehoge");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }


    @Test
    public void testInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/attend");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);
        
        addParameter(proxy, "userId", TestDataProvider.ATTENDANCE_UNKNOWN_USER_ID);
        addParameter(proxy, "eventId", TestDataProvider.DEFAULT_EVENT_ID);
        addParameter(proxy, "status", "present");
        addInvalidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }

}
