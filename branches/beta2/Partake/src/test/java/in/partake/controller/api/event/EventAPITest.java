package in.partake.controller.api.event;

import org.junit.Ignore;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

import in.partake.controller.api.APIControllerTest;
import in.partake.model.fixture.TestDataProvider;
import in.partake.util.Util;

public class EventAPITest extends APIControllerTest {

    @Test
    public void testGetEvent() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", TestDataProvider.EVENT_ID1);
        
        proxy.execute();
        assertResultOK(proxy);
    }
    
    @Test
    public void testGetEventWithoutId() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/");
        // addParameter(proxy, "eventId", TestDataProvider.EVENT_ID1);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }
    
    @Test
    public void testGetEventWithInvalidId() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", "invalid-event-id");
        
        proxy.execute();
        assertResultInvalid(proxy);
    }
    
    @Test
    public void testGetPrivateEvent() throws Exception {
        // If a private event is requested without login,
        // 'forbidden' should be returned.
        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", TestDataProvider.EVENT_PRIVATE_ID1);
        loginAs(proxy, TestDataProvider.USER_ID1);
        
        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testGetPrivateEventWithoutLogin() throws Exception {
        // If a private event is requested without login,
        // 'forbidden' should be returned.
        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", TestDataProvider.EVENT_PRIVATE_ID1);
        
        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testGetPrivateEventWithInvalidLogin() throws Exception {
        // If a private event is requested with invalid user,
        // 'forbidden' should be returned.
        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", TestDataProvider.EVENT_PRIVATE_ID1);
        loginAs(proxy, TestDataProvider.USER_ID3);
        
        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testGetPrivateEventWithCorrectPasscode() throws Exception {
        // Event if not logged in, when the correct passcode is provided,
        // 'get' API should succeed. 
        
        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", TestDataProvider.EVENT_PRIVATE_ID1);
        addParameter(proxy, "passcode", "passcode");
        
        proxy.execute();
        assertResultOK(proxy);
    }
    
    @Test
    public void testGetPrivateEventwithInvalidPasscode() throws Exception {
        // When invalid passcode is provided, 'forbidden' should be returned.
        ActionProxy proxy = getActionProxy("/api/event/");
        addParameter(proxy, "eventId", TestDataProvider.EVENT_PRIVATE_ID1);
        addParameter(proxy, "passcode", "invalid-passcode");
        
        proxy.execute();
        assertResultForbidden(proxy);
    }
    
    @Test
    @Ignore("Not implemented yet")
    public void testToCreateAnEventWithLogin() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToCreateAnEventWithoutLogin() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToCreateAnEventWithLoginWithInavlidSessionToken() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToModifyAnEventWithLogin() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToModifyAnEventWithoutLogin() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToModifyAnEventWithLoginWithInavlidSessionToken() throws Exception {
        throw new RuntimeException();
    }
    
    @Test
    @Ignore("Not implemented yet")
    public void testToRemoveOwnedEvent() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToRemoveManagedEvent() throws Exception {
        // manager cannot remove the event.
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToRemoveNotOwnedEvent() throws Exception {
        // only owner can remove the event
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToRemoveAnEventWithoutLogin() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToRemoveAnEventWithLoginWithInavlidSessionToken() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToEnrollAnEventWithLogin() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToEnrollAnEventWithoutLogin() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToEnrollAnEventWithLoginWithInavlidSessionToken() throws Exception {
        throw new RuntimeException();
    }
    
    @Test
    @Ignore("Not implemented yet")
    public void testToGetParticipants() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToGetParticipantsOfPasscodedEvent() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToGetParticipantsOfInvalidEvent() throws Exception {
        throw new RuntimeException();
    }
    
    @Test
    @Ignore("Not implemented yet")
    public void testToGetComments() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToGetCommentsOfPasscodedEvent() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToGetCommentsOfInvalidEvent() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToGetMessages() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToGetMessagesOfPasscodedEvent() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToGetMessagesOfInvalidEvent() throws Exception {
        throw new RuntimeException();
    }
    
    @Test
    @Ignore("Not implemented yet")
    public void testToGetAttendanceOfOwnedEvent() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToGetAttendanceOfManagedEvent() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToGetAttendanceWithoutLogin() throws Exception {
        throw new RuntimeException();
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToGetAttendanceOfNotOwnedEvent() throws Exception {
        // only owner (or manager) can take attendance.
        throw new RuntimeException();
    }

    @Test
    public void testToSendMessageForOwnedEvent() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/sendMessage");

        loginAs(proxy, TestDataProvider.USER_ID1);
        addParameter(proxy, "eventId", TestDataProvider.EVENT_ID1);
        addParameter(proxy, "message", "hogehogehoge");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultOK(proxy);
        
        // TODO: Check DB. 
    }

    @Test
    public void testToSendLongMessage() throws Exception {
        String longMessage = Util.randomString(1024);

        ActionProxy proxy = getActionProxy("/api/event/sendMessage");

        loginAs(proxy, TestDataProvider.USER_ID1);
        addParameter(proxy, "eventId", TestDataProvider.EVENT_ID1);
        addParameter(proxy, "message", longMessage);
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    @Ignore("Not implemented yet")
    public void testToSendVeryLongMessage() throws Exception {
        throw new RuntimeException();
    }

    @Test
    public void testToSendMessageForManagedEvent() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/sendMessage");

        // User2 is an editor of Event2. 
        loginAs(proxy, TestDataProvider.USER_ID2);
        addParameter(proxy, "eventId", TestDataProvider.EVENT_ID2);
        addParameter(proxy, "message", "hogehogehoge");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultOK(proxy);
    }

    @Test
    public void testToSendMessageForNotOwnedEvent() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/sendMessage");
        
        loginAs(proxy, TestDataProvider.USER_ID2);
        addParameter(proxy, "eventId", TestDataProvider.EVENT_ID1);
        addParameter(proxy, "message", "hogehogehoge");
        addValidSessionTokenToParameter(proxy);
        
        proxy.execute();
        assertResultForbidden(proxy);
    }

    @Test
    public void testToSendMessageWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/sendMessage");

        addParameter(proxy, "eventId", TestDataProvider.EVENT_ID1);
        addParameter(proxy, "message", "hogehogehoge");
        
        proxy.execute();
        assertResultLoginRequired(proxy);
    }
}
