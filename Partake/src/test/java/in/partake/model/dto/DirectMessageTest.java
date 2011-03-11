package in.partake.model.dto;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DirectMessageTest extends AbstractPartakeModelTest<Message> {
    private Message[] samples;

    @Before
    public void createSamples() {
        Date now = new Date();
        samples = new Message[] {
            new Message(),
            new Message("id1", "userId1", "message1", "eventId1", now),
            new Message("id2", "userId2", "message2", "eventId2", now),
        };
    }
    
    @Test
    public void testCopyConstructor() {
        for (Message source : samples) {
            Assert.assertEquals(source, new Message(source));
        }
        
        for (Message lhs : samples) {
            for (Message rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }

	@Override
	protected Message createModel() {
		return new Message();
	}
}
