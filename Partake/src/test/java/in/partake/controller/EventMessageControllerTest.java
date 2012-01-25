package in.partake.controller;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

public class EventMessageControllerTest extends AbstractPartakeControllerTest {
    private static final int MAX_LENGTH = 100;

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    @Ignore("Should fail if not logged in.")
    public void testToSendMessage() {
        // TODO should login
        ActionProxy proxy = getActionProxy("/events/send");
        EventsMessageController controller = (EventsMessageController) proxy.getAction();
        controller.setEventId("eventId");
        controller.setMessage(createString(MAX_LENGTH));
        Assert.assertEquals(Action.SUCCESS, controller.send());
    }

    @Test
    @Ignore("Should fail if not logged in.")
    public void testToSendTooLongMessage() {
        // TODO should login
        ActionProxy proxy = getActionProxy("/events/send");
        EventsMessageController controller = (EventsMessageController) proxy.getAction();
        controller.setEventId("eventId");
        controller.setMessage(createString(MAX_LENGTH + 1));
        Assert.assertEquals(Action.INPUT, controller.send());
    }

    @Test
    @Ignore("Should fail if not logged in.")
    public void testToSendMessageContainsSurrogatePair() {
        // TODO should login
        ActionProxy proxy = getActionProxy("/events/send");
        EventsMessageController controller = (EventsMessageController) proxy.getAction();
        controller.setEventId("eventId");
        controller.setMessage(createString(MAX_LENGTH - 1) + "𩸽");
        Assert.assertEquals(Action.INPUT, controller.send());
    }

    private String createString(int length) {
        StringBuilder b = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            b.append('*');
        }
        return b.toString();
    }
}
