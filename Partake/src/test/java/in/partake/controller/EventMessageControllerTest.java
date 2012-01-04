package in.partake.controller;

import junit.framework.Assert;

import org.apache.struts2.StrutsTestCase;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

// TODO: should extends PartakeControllerTestCase
public class EventMessageControllerTest extends StrutsTestCase {
	private static final int MAX_LENGTH = 100;

	public void setUp() throws Exception {
		super.setUp();
	}

	public void testToSendMessage() {
		// TODO should login
		ActionProxy proxy = getActionProxy("/events/send");
		EventsMessageController controller = (EventsMessageController) proxy.getAction();
		controller.setEventId("eventId");
		controller.setMessage(createString(MAX_LENGTH));
		Assert.assertEquals(Action.SUCCESS, controller.send());
	}

	public void testToSendTooLongMessage() {
		// TODO should login
		ActionProxy proxy = getActionProxy("/events/send");
		EventsMessageController controller = (EventsMessageController) proxy.getAction();
		controller.setEventId("eventId");
		controller.setMessage(createString(MAX_LENGTH + 1));
		Assert.assertEquals(Action.INPUT, controller.send());
	}

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
