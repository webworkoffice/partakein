package in.partake.controller.action.image;

import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.impl.BinaryTestDataProvider;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class ImageActionTest extends AbstractPartakeControllerTest {

    @Test
    public void testToGetImage() throws Exception {
        ActionProxy proxy = getActionProxy("/images/" + BinaryTestDataProvider.IMAGE_ID1);

        proxy.execute();
        assertResultSuccess(proxy);

        Assert.assertArrayEquals(BinaryTestDataProvider.BYTE1_CONTENT, response.getContentAsByteArray());
   }

    @Test
    public void testToGetWithoutImageId() throws Exception {
        ActionProxy proxy = getActionProxy("/images/");

        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testToGetWithInvalidId() throws Exception {
        ActionProxy proxy = getActionProxy("/images/invalid");

        proxy.execute();
        assertResultInvalid(proxy);
    }

	@Test
	public void testToGetNonexistentImage() throws Exception {
        UUID uuid = new UUID(0, 0);
        ActionProxy proxy = getActionProxy("/images/" + uuid.toString());

        proxy.execute();
        assertResultNotFound(proxy);
	}	
}
