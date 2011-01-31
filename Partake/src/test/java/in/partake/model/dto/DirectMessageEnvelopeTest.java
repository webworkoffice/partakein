package in.partake.model.dto;

import in.partake.model.dto.aux.DirectMessagePostingType;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DirectMessageEnvelopeTest {
    private DirectMessageEnvelope[] samples;

    @Before
    public void createSampleData() {
        Date now = new Date(); 
        samples = new DirectMessageEnvelope[] {
                new DirectMessageEnvelope(),
                new DirectMessageEnvelope("id1", "senderId1", "receiverId1", "messageId1", now, 0, now, now, DirectMessagePostingType.POSTING_TWITTER_DIRECT),
                new DirectMessageEnvelope("id2", "senderId2", "receiverId2", "messageId2", now, 0, now, now, DirectMessagePostingType.POSTING_TWITTER_DIRECT),
        };
    }
    
    @Test
    public void testCopyConstructor() {
        for (DirectMessageEnvelope source : samples) {
            Assert.assertEquals(source, new DirectMessageEnvelope(source));
        }
        
        for (DirectMessageEnvelope lhs : samples) {
            for (DirectMessageEnvelope rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }
}
