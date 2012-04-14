package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.base.TimeUtil;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ImageDataTest extends AbstractPartakeModelTest<ImageData> {
    @Override
    protected ImageData copy(ImageData t) {
        return new ImageData(t);
    }

    @Override
    protected TestDataProvider<ImageData> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getImageProvider();
    }

    private ImageData[] samples;

    @Before
    public void createSampleData() {
        samples = new ImageData[] {
                new ImageData(),
                new ImageData("id1", "userId1", "something", new byte[] { -1, 0, 1 }, TimeUtil.getCurrentDate()),
                new ImageData("id2", "userId2", "somewhere", new byte[] { 0, 1, 2, 3, 4 }, TimeUtil.getCurrentDate()),
        };
    }

    @Test
    public void testCopyConstructor() {
        for (ImageData source : samples) {
            Assert.assertEquals(source, new ImageData(source));
        }

        for (ImageData lhs : samples) {
            for (ImageData rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }

}
