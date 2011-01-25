package in.partake.model.dto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FeedLinkageTest {
    private FeedLinkage[] samples;

    @Before
    public void createSamples() {
        samples = new FeedLinkage[] {
            new FeedLinkage(),
            new FeedLinkage("id1", "hoge"),
            new FeedLinkage("id2", "fuga"),
        };
    }
    
    @Test
    public void testCopyConstructor() {
        for (FeedLinkage source : samples) {
            Assert.assertEquals(source, new FeedLinkage(source));
        }
        
        for (FeedLinkage lhs : samples) {
            for (FeedLinkage rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }

}
