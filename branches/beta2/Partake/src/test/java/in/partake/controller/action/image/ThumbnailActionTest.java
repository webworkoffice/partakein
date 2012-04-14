package in.partake.controller.action.image;

import in.partake.controller.AbstractPartakeControllerTest;

import java.util.UUID;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class ThumbnailActionTest extends AbstractPartakeControllerTest {

    @Test
    public void testToGetImage() throws Exception {
        ActionProxy proxy = getActionProxy("/images/thumbnail/" + DEFAULT_IMAGE_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        // TODO: do some test here.
   }

    @Test
    public void testToGetWithoutImageId() throws Exception {
        ActionProxy proxy = getActionProxy("/images/thumbnail/");

        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testToGetWithInvalidId() throws Exception {
        ActionProxy proxy = getActionProxy("/images/thumbnail/invalid");

        proxy.execute();
        assertResultInvalid(proxy);
    }

    @Test
    public void testToGetNonexistentImage() throws Exception {
        UUID uuid = new UUID(0, 0);
        ActionProxy proxy = getActionProxy("/images/thumbnail/" + uuid.toString());

        proxy.execute();
        assertResultNotFound(proxy);
    }
}
