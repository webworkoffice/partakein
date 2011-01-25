package in.partake.model.dto;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DirectMessageTest {
    private DirectMessage[] samples;

    @Before
    public void createSamples() {
        Date now = new Date();
        samples = new DirectMessage[] {
            new DirectMessage(),
            new DirectMessage("id1", "userId1", "message", "eventId", now),
            new DirectMessage("id1", "userId1", "message", "eventId", now),
        };
    }
    
    @Test
    public void testCopyConstructor() {
        for (DirectMessage source : samples) {
            Assert.assertEquals(source, new DirectMessage(source));
        }
        
        for (DirectMessage lhs : samples) {
            for (DirectMessage rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }
}
