package in.partake.model.dto;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DirectMessageTest extends AbstractPartakeModelTest<DirectMessage> {
    private DirectMessage[] samples;

    @Before
    public void createSamples() {
        Date now = new Date();
        samples = new DirectMessage[] {
            new DirectMessage(),
            new DirectMessage("id1", "userId1", "message1", "eventId1", now),
            new DirectMessage("id2", "userId2", "message2", "eventId2", now),
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

	@Override
	protected DirectMessage createModel() {
		return new DirectMessage();
	}
}
