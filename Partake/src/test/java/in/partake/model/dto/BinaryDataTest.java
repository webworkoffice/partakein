package in.partake.model.dto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BinaryDataTest {
    private BinaryData[] samples;
    
    @Before
    public void createSampleData() {
        samples = new BinaryData[] {
                new BinaryData(),
                new BinaryData("id1", "something", new byte[] { -1, 0, 1 }),
                new BinaryData("id2", "somewhere", new byte[] { 0, 1, 2, 3, 4 }),
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

}
