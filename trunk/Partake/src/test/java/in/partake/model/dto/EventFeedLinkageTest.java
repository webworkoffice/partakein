package in.partake.model.dto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EventFeedLinkageTest {
    private EventFeedLinkage[] samples;

    @Before
    public void createSamples() {
        samples = new EventFeedLinkage[] {
            new EventFeedLinkage(),
            new EventFeedLinkage("id1", "hoge"),
            new EventFeedLinkage("id2", "fuga"),
        };
    }
    
    @Test
    public void testCopyConstructor() {
        for (EventFeedLinkage source : samples) {
            Assert.assertEquals(source, new EventFeedLinkage(source));
        }
        
        for (EventFeedLinkage lhs : samples) {
            for (EventFeedLinkage rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }

}
