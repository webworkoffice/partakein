package in.partake.controller.action.image;

import in.partake.base.Util;
import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.impl.BinaryTestDataProvider;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class ImageActionTest extends AbstractPartakeControllerTest {

    @Test
    @Ignore("EventTestDataProvider should provide images.")
    public void testToGetImage() throws Exception {
        ActionProxy proxy = getActionProxy("/events/images/" + BinaryTestDataProvider.IMAGE_ID1);

        proxy.execute();
        assertResultSuccess(proxy);

        ImageAction action = (ImageAction) proxy.getAction();
        
        byte[] data = Util.getContentOfInputStream(action.getInputStream());
        Assert.assertArrayEquals(BinaryTestDataProvider.BYTE1_CONTENT, data);
   }

    @Test
    public void testToGetWithoutImageId() throws Exception {
        ActionProxy proxy = getActionProxy("/events/images/");

        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testToGetWithInvalidId() throws Exception {
        ActionProxy proxy = getActionProxy("/events/images/invalid");

        proxy.execute();
        assertResultInvalid(proxy);
    }

	@Test
	public void testToGetNonexistentImage() throws Exception {
        UUID uuid = new UUID(0, 0);
        ActionProxy proxy = getActionProxy("/events/images/" + uuid.toString());

        proxy.execute();
        assertResultNotFound(proxy);
	}	
}
