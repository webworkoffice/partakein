package in.partake.controller.action.image;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.action.ActionControllerTest;

import java.util.UUID;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class ThumbnailActionTest extends ActionControllerTest {

    @Test
    public void testToGetImage() throws Exception {
        ActionProxy proxy = getActionProxy("/images/thumbnail/" + DEFAULT_IMAGE_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        // TODO: do some test here.
    }

    @Test
    public void testToGetImageHavingNoThumbnail() throws Exception {
        ActionProxy proxy = getActionProxy("/images/thumbnail/" + IMAGE_HAVING_NO_THUMBNAIL_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        assertThat(loadThumbnail(IMAGE_HAVING_NO_THUMBNAIL_ID), is(notNullValue()));
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
