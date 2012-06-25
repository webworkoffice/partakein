package in.partake.controller.action.image;

import in.partake.app.PartakeApp;
import in.partake.controller.action.ActionControllerTest;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class ImageActionTest extends ActionControllerTest {

    @Test
    public void testToGetImage() throws Exception {
        ActionProxy proxy = getActionProxy("/images/" + DEFAULT_IMAGE_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        byte[] array = PartakeApp.getTestService().getTestDataProviderSet().getImageProvider().getDefaultImageContent();
        Assert.assertArrayEquals(array, response.getContentAsByteArray());
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
