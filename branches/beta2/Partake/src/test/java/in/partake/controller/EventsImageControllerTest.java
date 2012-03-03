package in.partake.controller;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class EventsImageControllerTest extends AbstractPartakeControllerTest {
	@Test
	public void testToShowWithoutImageId() {
		request.removeParameter(EventsImageController.IMAGE_ID_PARAM_NAME);

		ActionProxy proxy = getActionProxy("/events/images/");
		EventsImageController controller = (EventsImageController) proxy.getAction();
		Assert.assertEquals(PartakeActionSupport.NOT_FOUND, controller.show());
	}

	@Test
	public void testToGetNonexistentImage() {
		request.setParameter(EventsImageController.IMAGE_ID_PARAM_NAME, "NonexistentImageId");

		ActionProxy proxy = getActionProxy("/events/images/");
		EventsImageController controller = (EventsImageController) proxy.getAction();
		Assert.assertEquals(PartakeActionSupport.NOT_FOUND, controller.show());
	}
}
