package in.partake.model.dto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EventRelationTest extends AbstractPartakeModelTest<EventRelation> {
    private EventRelation[] samples;

    @Before
    public void createSamples() {
        samples = new EventRelation[] {
            new EventRelation(),
            new EventRelation("srcEventId", "eventId1", true, false),
            new EventRelation("srcEventId", "eventId2", true, false),
        };
    }
    
    @Test
    public void testCopyConstructor() {
        for (EventRelation source : samples) {
            Assert.assertEquals(source, new EventRelation(source));
        }
        
        for (EventRelation lhs : samples) {
            for (EventRelation rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }

	@Override
	protected EventRelation createModel() {
		return new EventRelation();
	}
}
