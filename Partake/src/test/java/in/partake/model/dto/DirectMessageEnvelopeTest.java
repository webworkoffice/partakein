package in.partake.model.dto;

import in.partake.model.dto.auxiliary.DirectMessagePostingType;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DirectMessageEnvelopeTest {
    private Envelope[] samples;

    @Before
    public void createSampleData() {
        Date now = new Date(); 
        samples = new Envelope[] {
                new Envelope(),
                new Envelope("id1", "senderId1", "receiverId1", "messageId1", now, 0, now, now, DirectMessagePostingType.POSTING_TWITTER_DIRECT, now),
                new Envelope("id2", "senderId2", "receiverId2", "messageId2", now, 0, now, now, DirectMessagePostingType.POSTING_TWITTER_DIRECT, now),
        };
    }
    
    @Test
    public void testCopyConstructor() {
        for (Envelope source : samples) {
            Assert.assertEquals(source, new Envelope(source));
        }
        
        for (Envelope lhs : samples) {
            for (Envelope rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }
}
