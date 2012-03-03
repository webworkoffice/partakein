package in.partake.model.dto;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BinaryDataTest extends AbstractPartakeModelTest<BinaryData> {
    private BinaryData[] samples;
    
    @Before
    public void createSampleData() {
        samples = new BinaryData[] {
                new BinaryData(),
                new BinaryData("id1", "user1", "something", new byte[] { -1, 0, 1 }, new Date()),
                new BinaryData("id2", "user2", "somewhere", new byte[] { 0, 1, 2, 3, 4 }, new Date()),
        };
    }
    
    @Test
    public void testCopyConstructor() {
        for (BinaryData source : samples) {
            Assert.assertEquals(source, new BinaryData(source));
        }
        
        for (BinaryData lhs : samples) {
            for (BinaryData rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }

	@Override
	protected BinaryData createModel() {
		return new BinaryData();
	}

}
