package in.partake.model.dto;

import in.partake.base.TimeUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ImageDataTest extends AbstractPartakeModelTest<ImageData> {
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

	@Override
	protected ImageData createModel() {
		return new ImageData();
	}

}
